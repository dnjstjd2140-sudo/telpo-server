package com.telpo.server.map.kakao;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.telpo.server.common.LatLng;
import com.telpo.server.map.MapProvider;
import com.telpo.server.map.PlaceCandidate;

/**
 * 1단계(국내) 장소 검색 프로바이더 구현. 카카오 로컬 REST API를 사용한다.
 * 카카오모빌리티는 자동차 경로만 지원하므로 경로 계산은 {@code TmapRouteProvider}가 담당한다.
 */
@Component
public class KakaoMapProvider implements MapProvider {

    private static final String LOCAL_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

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
}
