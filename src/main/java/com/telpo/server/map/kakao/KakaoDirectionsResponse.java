package com.telpo.server.map.kakao;

import java.util.List;

/**
 * 카카오모빌리티 자동차 길찾기 API 응답 (필요한 필드만).
 * https://developers.kakaomobility.com/docs/navi-api/directions/
 */
record KakaoDirectionsResponse(List<Route> routes) {

    record Route(Summary summary) {
    }

    record Summary(long distance, long duration) {
    }
}
