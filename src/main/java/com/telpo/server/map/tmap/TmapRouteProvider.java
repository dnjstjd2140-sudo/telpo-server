package com.telpo.server.map.tmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import tools.jackson.databind.JsonNode;

import com.telpo.server.common.LatLng;
import com.telpo.server.common.exception.NotFoundException;
import com.telpo.server.map.DistanceMatrix;
import com.telpo.server.map.Route;
import com.telpo.server.map.RouteProvider;
import com.telpo.server.map.TravelMode;

/**
 * 국내 경로 프로바이더. Tmap Open API로 자동차/도보/대중교통을 모두 처리한다.
 *
 * <p>모든 좌표는 WGS84이며 Tmap 좌표는 [경도, 위도] 순서다.
 * 도보/대중교통은 구간(waypoint 쌍)별로 호출해 이어 붙인다.
 * 무료 플랜은 경로안내 1,000건/일이라 호출 수에 유의한다(getMatrix는 N*(N-1)회 호출).
 */
@Component
public class TmapRouteProvider implements RouteProvider {

    private static final String CAR_URL = "https://apis.openapi.sk.com/tmap/routes?version=1";
    private static final String PEDESTRIAN_URL = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1";
    private static final String TRANSIT_URL = "https://apis.openapi.sk.com/transit/routes";

    /** 출발지/도착지가 너무 가까워 대중교통 경로가 없을 때 Tmap이 돌려주는 코드. */
    private static final int TRANSIT_TOO_CLOSE = 11;

    private static final long CACHE_TTL_MILLIS = 30 * 60 * 1000L;
    private static final int CACHE_MAX_ENTRIES = 500;

    private record CacheKey(List<LatLng> waypoints, TravelMode mode) {
    }

    private record CacheEntry(Route route, long expiresAt) {
    }

    /** 최대 크기를 넘으면 가장 오래된 항목부터 제거하는 단순 LRU 캐시. */
    private final Map<CacheKey, CacheEntry> cache = Collections.synchronizedMap(
            new LinkedHashMap<>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<CacheKey, CacheEntry> eldest) {
                    return size() > CACHE_MAX_ENTRIES;
                }
            });

    private final RestClient restClient;
    private final TmapProperties properties;

    public TmapRouteProvider(TmapProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .defaultHeader("appKey", properties.appKey() == null ? "" : properties.appKey())
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Override
    public Route getRoute(List<LatLng> waypoints, TravelMode mode) {
        if (waypoints.size() < 2) {
            throw new IllegalArgumentException("경로 계산에는 출발지/도착지가 최소 2개 필요합니다.");
        }
        if (properties.appKey() == null || properties.appKey().isBlank()) {
            throw new IllegalStateException("TMAP_APP_KEY가 설정되지 않았습니다 (.env 확인)");
        }

        // 같은 요청의 반복 호출(최적화 매트릭스, 새로고침 등)로 무료 한도를 소진하지 않도록 캐싱한다.
        CacheKey key = new CacheKey(List.copyOf(waypoints), mode);
        long now = System.currentTimeMillis();
        CacheEntry cached = cache.get(key);
        if (cached != null && cached.expiresAt() > now) {
            return cached.route();
        }

        Route route = fetchRoute(waypoints, mode);
        cache.put(key, new CacheEntry(route, now + CACHE_TTL_MILLIS));
        return route;
    }

    private Route fetchRoute(List<LatLng> waypoints, TravelMode mode) {
        if (mode == TravelMode.CAR) {
            return carRoute(waypoints);
        }

        List<LatLng> path = new ArrayList<>();
        long distance = 0;
        long duration = 0;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Route leg = mode == TravelMode.WALK
                    ? walkRoute(waypoints.get(i), waypoints.get(i + 1))
                    : transitRoute(waypoints.get(i), waypoints.get(i + 1));
            path.addAll(leg.path());
            distance += leg.distanceMeters();
            duration += leg.durationSeconds();
        }
        return new Route(path, distance, duration);
    }

    @Override
    public DistanceMatrix getMatrix(List<LatLng> points) {
        int n = points.size();
        long[][] durations = new long[n][n];
        long[][] distances = new long[n][n];

        // 쌍별 호출 결과는 getRoute 캐시에 쌓인다. 첫 호출은 여전히 N*(N-1)건이므로
        // 지점이 많아지면 Tmap 경로 매트릭스 API(별도 상품)로 교체할 것 (무료 한도 1,000건/일).
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                Route route = getRoute(List.of(points.get(i), points.get(j)), TravelMode.CAR);
                durations[i][j] = route.durationSeconds();
                distances[i][j] = route.distanceMeters();
            }
        }
        return new DistanceMatrix(points, durations, distances);
    }

    private Route carRoute(List<LatLng> waypoints) {
        LatLng origin = waypoints.get(0);
        LatLng destination = waypoints.get(waypoints.size() - 1);

        Map<String, Object> body = baseBody(origin, destination);
        body.put("searchOption", "0"); // 추천 경로
        List<LatLng> middle = waypoints.subList(1, waypoints.size() - 1);
        if (!middle.isEmpty()) {
            // 경유지 "경도,위도_경도,위도" (최대 5개)
            body.put("passList", String.join("_", middle.stream().map(p -> p.lng() + "," + p.lat()).toList()));
        }

        return parseGeoJson(post(CAR_URL, body));
    }

    private Route walkRoute(LatLng from, LatLng to) {
        Map<String, Object> body = baseBody(from, to);
        body.put("startName", "start");
        body.put("endName", "end");
        return parseGeoJson(post(PEDESTRIAN_URL, body));
    }

    private Route transitRoute(LatLng from, LatLng to) {
        Map<String, Object> body = new HashMap<>();
        body.put("startX", String.valueOf(from.lng()));
        body.put("startY", String.valueOf(from.lat()));
        body.put("endX", String.valueOf(to.lng()));
        body.put("endY", String.valueOf(to.lat()));
        body.put("lang", 0);
        body.put("format", "json");
        body.put("count", 1);

        JsonNode response = post(TRANSIT_URL, body);

        if (response.path("result").path("status").asInt(-1) == TRANSIT_TOO_CLOSE) {
            // 너무 가까우면 대중교통 경로가 없으므로 도보로 대체한다.
            return walkRoute(from, to);
        }

        JsonNode itinerary = response.path("metaData").path("plan").path("itineraries").path(0);
        if (itinerary.isMissingNode()) {
            throw new NotFoundException("대중교통 경로를 찾을 수 없습니다.");
        }

        List<LatLng> path = new ArrayList<>();
        for (JsonNode leg : itinerary.path("legs")) {
            JsonNode passShape = leg.path("passShape").path("linestring");
            if (passShape.isString()) {
                path.addAll(parseLineString(passShape.asString()));
            } else {
                // 도보 구간은 steps[].linestring 으로 내려온다.
                for (JsonNode step : leg.path("steps")) {
                    path.addAll(parseLineString(step.path("linestring").asString("")));
                }
            }
        }
        if (path.isEmpty()) {
            path.add(from);
            path.add(to);
        }

        return new Route(path, itinerary.path("totalDistance").asLong(0), itinerary.path("totalTime").asLong(0));
    }

    private Map<String, Object> baseBody(LatLng from, LatLng to) {
        Map<String, Object> body = new HashMap<>();
        body.put("startX", String.valueOf(from.lng()));
        body.put("startY", String.valueOf(from.lat()));
        body.put("endX", String.valueOf(to.lng()));
        body.put("endY", String.valueOf(to.lat()));
        body.put("reqCoordType", "WGS84GEO");
        body.put("resCoordType", "WGS84GEO");
        return body;
    }

    private JsonNode post(String url, Map<String, Object> body) {
        JsonNode response = restClient.post()
                .uri(url)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
        if (response == null) {
            throw new IllegalStateException("Tmap 응답이 비어 있습니다.");
        }
        return response;
    }

    /** 자동차/보행자 GeoJSON: LineString feature 좌표를 이어 붙이고 첫 feature의 totalDistance/totalTime을 쓴다. */
    private Route parseGeoJson(JsonNode response) {
        JsonNode features = response.path("features");
        if (!features.isArray() || features.isEmpty()) {
            throw new NotFoundException("경로를 찾을 수 없습니다.");
        }

        List<LatLng> path = new ArrayList<>();
        for (JsonNode feature : features) {
            JsonNode geometry = feature.path("geometry");
            if (!"LineString".equals(geometry.path("type").asString())) {
                continue;
            }
            for (JsonNode coord : geometry.path("coordinates")) {
                LatLng point = new LatLng(coord.path(1).asDouble(), coord.path(0).asDouble()); // [lng, lat]
                if (path.isEmpty() || !path.get(path.size() - 1).equals(point)) {
                    path.add(point);
                }
            }
        }

        JsonNode summary = features.path(0).path("properties");
        return new Route(path, summary.path("totalDistance").asLong(0), summary.path("totalTime").asLong(0));
    }

    /** "경도,위도 경도,위도 ..." 형식의 linestring 파싱. */
    private List<LatLng> parseLineString(String linestring) {
        List<LatLng> points = new ArrayList<>();
        if (linestring == null || linestring.isBlank()) {
            return points;
        }
        for (String pair : linestring.trim().split("\\s+")) {
            String[] xy = pair.split(",");
            if (xy.length == 2) {
                points.add(new LatLng(Double.parseDouble(xy[1]), Double.parseDouble(xy[0])));
            }
        }
        return points;
    }
}
