package com.telpo.server.map;

import java.util.List;

import com.telpo.server.common.LatLng;

/**
 * @param taxiFare 자차 경로일 때 택시 예상 요금(원). 그 외에는 null.
 * @param tollFare 자차 경로일 때 통행료(원). 그 외에는 null.
 * @param transitFare 대중교통일 때 총 요금(원). 그 외에는 null.
 * @param legs 대중교통일 때 도보/버스/지하철 구간 목록. 그 외에는 빈 리스트.
 */
public record Route(
        List<LatLng> path,
        long distanceMeters,
        long durationSeconds,
        Integer taxiFare,
        Integer tollFare,
        Integer transitFare,
        List<TransitLeg> legs
) {
    public Route(List<LatLng> path, long distanceMeters, long durationSeconds) {
        this(path, distanceMeters, durationSeconds, null, null, null, List.of());
    }
}
