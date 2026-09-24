package com.telpo.server.map;

import java.util.List;

import com.telpo.server.common.LatLng;

public record Route(
        List<LatLng> path,
        long distanceMeters,
        long durationSeconds
) {
}