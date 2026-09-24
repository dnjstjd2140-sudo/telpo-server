package com.telpo.server.place;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByProviderAndProviderPlaceId(String provider, String providerPlaceId);
}