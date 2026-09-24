package com.telpo.server.route.dto;

import java.util.List;

import com.telpo.server.common.LatLng;
import com.telpo.server.map.Route;

public record RouteResponse(
        List<LatLng> path,
        long distanceMeters,
        long durationSeconds
) {
    public static RouteResponse from(Route route) {
        return new RouteResponse(route.path(), route.distanceMeters(), route.durationSeconds());
    }
}
