package com.telpo.server.trip.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTripRequest(
        @NotBlank String title,
        LocalDate startDate,
        LocalDate endDate,
        @NotNull List<UpdateTripStopRequest> stops
) {
}
