package com.telpo.server.trip.dto;

import com.telpo.server.trip.TripStop;

public record TripStopResponse(
        Long placeId,
        String name,
        String address,
        double lat,
        double lng,
        int dayNo,
        int orderNo,
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
                stop.getDayNo(),
                stop.getOrderNo(),
                stop.getMemo(),
                stop.getStayMinutes()
        );
    }
}