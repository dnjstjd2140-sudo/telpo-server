package com.telpo.server.map.kakao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.telpo.server.common.LatLng;
import com.telpo.server.map.DistanceMatrix;
import com.telpo.server.map.MapProvider;
import com.telpo.server.map.PlaceCandidate;
import com.telpo.server.map.Route;
import com.telpo.server.map.TravelMode;

/**
 * 1단계(국내) 지도 프로바이더 구현. 카카오 로컬(검색) + 카카오모빌리티(길찾기) REST API를 사용한다.
 *
 * <p>getRoute/getMatrix는 현재 자동차(CAR) 경로만 지원한다. 도보/대중교통은
 * 명세서 12장 미결정 사항(API 제공 범위 확인 필요) 확정 후 추가한다.
 */
@Component
public class KakaoMapProvider implements MapProvider {

    private static final String LOCAL_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";
    private static final String DIRECTIONS_URL = "https://apis-navi.kakaomobility.com/v1/directions";

    private final RestClient restClient;
    private final KakaoProperties properties;

    public KakaoMapProvider(KakaoProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .defaultHeader("Authorization", "KakaoAK " + properties.restApiKey())
                .build();
    }

    @Override
    public List<PlaceCandidate> searchPlaces(String keyword, LatLng near) {
        String uri = UriComponentsBuilder.fromUriString(LOCAL_SEARCH_URL)
                .queryParam("query", keyword)
                .queryParam("x", near.lng())
                .queryParam("y", near.lat())
                .build()
                .toUriString();

        KakaoLocalSearchResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(KakaoLocalSearchResponse.class);

        if (response == null) {
            return List.of();
        }

        return response.documents().stream()
                .map(doc -> new PlaceCandidate(
                        doc.place_name(),
                        doc.address_name(),
                        new LatLng(Double.parseDouble(doc.y()), Double.parseDouble(doc.x())),
                        "KAKAO",
                        doc.id()
                ))
                .toList();
    }

    @Override
    public Route getRoute(List<LatLng> waypoints, TravelMode mode) {
        if (mode != TravelMode.CAR) {
            throw new UnsupportedOperationException("카카오 프로바이더는 현재 자동차(CAR) 경로만 지원합니다: " + mode);
        }
        if (waypoints.size() < 2) {
            throw new IllegalArgumentException("경로 계산에는 출발지/도착지가 최소 2개 필요합니다.");
        }

        LatLng origin = waypoints.get(0);
        LatLng destination = waypoints.get(waypoints.size() - 1);
        List<LatLng> middle = waypoints.subList(1, waypoints.size() - 1);

        var uriBuilder = new StringBuilder(DIRECTIONS_URL)
                .append("?origin=").append(origin.lng()).append(',').append(origin.lat())
                .append("&destination=").append(destination.lng()).append(',').append(destination.lat());
        if (!middle.isEmpty()) {
            uriBuilder.append("&waypoints=");
            for (int i = 0; i < middle.size(); i++) {
                if (i > 0) {
                    uriBuilder.append('|');
                }
                uriBuilder.append(middle.get(i).lng()).append(',').append(middle.get(i).lat());
            }
        }

        KakaoDirectionsResponse response = restClient.get()
                .uri(uriBuilder.toString())
                .retrieve()
                .body(KakaoDirectionsResponse.class);

        if (response == null || response.routes().isEmpty()) {
            throw new IllegalStateException("카카오모빌리티 길찾기 응답이 비어 있습니다.");
        }

        KakaoDirectionsResponse.Summary summary = response.routes().get(0).summary();
        // TODO: routes[0].sections[].roads[].vertexes 를 파싱해 실제 도로 선형(polyline)을 채울 것.
        // 지금은 경유지 좌표를 그대로 path로 사용한다.
        return new Route(new ArrayList<>(waypoints), summary.distance(), summary.duration());
    }

    @Override
    public DistanceMatrix getMatrix(List<LatLng> points) {
        int n = points.size();
        long[][] durations = new long[n][n];
        long[][] distances = new long[n][n];

        // TODO: 카카오모빌리티 다중 목적지 API 또는 배치 호출로 교체하고 결과를 캐싱할 것 (호출 비용 큼).
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
}