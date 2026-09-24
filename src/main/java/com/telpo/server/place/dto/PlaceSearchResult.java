package com.telpo.server.place.dto;

import com.telpo.server.map.PlaceCandidate;

public record PlaceSearchResult(
        String name,
        String address,
        double lat,
        double lng,
        String provider,
        String providerPlaceId
) {
    public static PlaceSearchResult from(PlaceCandidate candidate) {
        return new PlaceSearchResult(
                candidate.name(),
                candidate.address(),
                candidate.location().lat(),
                candidate.location().lng(),
                candidate.provider(),
                candidate.providerPlaceId()
        );
    }
}