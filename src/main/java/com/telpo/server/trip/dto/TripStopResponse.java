package com.telpo.server.trip.dto;

import com.telpo.server.map.TravelMode;
import com.telpo.server.trip.TripStop;

public record TripStopResponse(
        Long placeId,
        String name,
        String address,
        double lat,
        double lng,
        String provider,
        String providerPlaceId,
        int dayNo,
        int orderNo,
        TravelMode travelMode,
        String memo,
        Integer stayMinutes
) {
    public static TripStopResponse from(TripStop stop) {
        return new TripStopResponse(
                stop.getPlace().getId(),
                stop.getPlace().getName(),
                stop.getPlace().getAddress(),
                stop.getPlace().getLat(),
                stop.getPlace().getLng(),
                stop.getPlace().getProvider(),
                stop.getPlace().getProviderPlaceId(),
                stop.getDayNo(),
                stop.getOrderNo(),
                stop.getTravelMode(),
                stop.getMemo(),
                stop.getStayMinutes()
        );
    }
}