package com.telpo.server.trip.dto;

/**
 * editToken은 이 응답에서만 노출된다. 서버는 해시만 저장하므로 이후에는 복구할 수 없다
 * — 프론트가 즉시 localStorage에 저장해야 한다 (명세서 5장).
 */
public record CreateTripResponse(
        Long tripId,
        String editToken,
        String shareToken
) {
}
