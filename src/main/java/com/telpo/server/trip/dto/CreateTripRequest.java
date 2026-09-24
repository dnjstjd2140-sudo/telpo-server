package com.telpo.server.trip.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record CreateTripRequest(
        @NotBlank String title,
        @NotBlank String countryCode,
        LocalDate startDate,
        LocalDate endDate
) {
}
