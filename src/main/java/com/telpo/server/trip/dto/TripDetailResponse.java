package com.telpo.server.trip.dto;

import java.time.LocalDate;
import java.util.List;

import com.telpo.server.trip.Trip;
import com.telpo.server.trip.TripStop;

public record TripDetailResponse(
        Long tripId,
        String title,
        String countryCode,
        LocalDate startDate,
        LocalDate endDate,
        boolean readOnly,
        String shareToken,
        List<TripStopResponse> stops
) {
    public static TripDetailResponse of(Trip trip, List<TripStop> stops, boolean readOnly) {
        return new TripDetailResponse(
                trip.getId(),
                trip.getTitle(),
                trip.getCountryCode(),
                trip.getStartDate(),
                trip.getEndDate(),
                readOnly,
                trip.getShareToken(),
                stops.stream().map(TripStopResponse::from).toList()
        );
    }
}