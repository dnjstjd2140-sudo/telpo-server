package com.telpo.server.map;

/**
 * 대중교통 경로의 한 구간. mode는 Tmap 값 그대로 (WALK, BUS, SUBWAY, EXPRESSBUS, TRAIN ...).
 *
 * @param pathStart 이 구간이 Route.path 에서 차지하는 첫 좌표 index (포함)
 * @param pathEnd 이 구간이 Route.path 에서 차지하는 마지막 좌표 index (포함). 좌표가 없으면 pathStart보다 작다.
 */
public record TransitLeg(
        String mode,
        String name,
        String color,
        String startName,
        String endName,
        long durationSeconds,
        int pathStart,
        int pathEnd
) {
}
