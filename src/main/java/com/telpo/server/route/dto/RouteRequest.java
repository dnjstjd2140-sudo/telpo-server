package com.telpo.server.route.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.telpo.server.common.LatLng;
import com.telpo.server.map.TravelMode;

public record RouteRequest(
        @NotNull @Size(min = 2) List<LatLng> waypoints,
        @NotNull TravelMode mode
) {
}
