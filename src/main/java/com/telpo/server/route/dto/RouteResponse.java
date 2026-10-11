package com.telpo.server.route.dto;

import java.util.List;

import com.telpo.server.common.LatLng;
import com.telpo.server.map.Route;
import com.telpo.server.map.TransitLeg;

public record RouteResponse(
        List<LatLng> path,
        long distanceMeters,
        long durationSeconds,
        Integer taxiFare,
        Integer tollFare,
        Integer transitFare,
        List<TransitLeg> legs
) {
    public static RouteResponse from(Route route) {
        return new RouteResponse(
                route.path(),
                route.distanceMeters(),
                route.durationSeconds(),
                route.taxiFare(),
                route.tollFare(),
                route.transitFare(),
                route.legs());
    }
}
