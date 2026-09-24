package com.telpo.server.map;

import java.util.List;

import com.telpo.server.common.LatLng;

/**
 * points[i] -> points[j] 이동 거리/시간. durationsSeconds[i][j], distancesMeters[i][j].
 */
public record DistanceMatrix(
        List<LatLng> points,
        long[][] durationsSeconds,
        long[][] distancesMeters
) {
}