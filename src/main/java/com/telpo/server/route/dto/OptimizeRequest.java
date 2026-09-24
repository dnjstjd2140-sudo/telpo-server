package com.telpo.server.route.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.telpo.server.common.LatLng;

public record OptimizeRequest(
        @NotNull @Size(min = 2) List<LatLng> points
) {
}
