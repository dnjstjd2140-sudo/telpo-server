package com.telpo.server.map;

import com.telpo.server.common.LatLng;

/**
 * 지도 프로바이더 검색 결과 1건. 여행에 추가되면 {@code Place} 엔티티로 변환된다.
 */
public record PlaceCandidate(
        String name,
        String address,
        LatLng location,
        String provider,
        String providerPlaceId
) {
}