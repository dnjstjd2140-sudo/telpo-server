package com.telpo.server.map.kakao;

import java.util.List;

/**
 * 카카오 로컬 키워드 검색 API 응답 (필요한 필드만).
 * https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword
 */
record KakaoLocalSearchResponse(List<Document> documents) {

    record Document(String id, String place_name, String address_name, String x, String y) {
    }
}
