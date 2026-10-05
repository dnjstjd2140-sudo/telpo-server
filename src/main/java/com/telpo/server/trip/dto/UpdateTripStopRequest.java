package com.telpo.server.trip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.telpo.server.map.TravelMode;

/**
 * 저장 시점의 장소 정보를 그대로 실어 보낸다 (검색 결과 그대로, 또는 기존 stop 그대로).
 * 서버가 (provider, providerPlaceId) 기준으로 Place를 재사용하거나 새로 만든다.
 */
public record UpdateTripStopRequest(
        @NotBlank String placeName,
        String placeAddress,
        double lat,
        double lng,
        @NotBlank String provider,
        @NotBlank String providerPlaceId,
        @NotNull Integer dayNo,
        @NotNull Integer orderNo,
        /** 이전 정류지에서 여기로 올 때의 이동수단. 비우면 CAR로 저장됨(그날 첫 정류지 등). */
        TravelMode travelMode,
        String memo,
        Integer stayMinutes
) {
}
