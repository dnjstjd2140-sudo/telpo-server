package com.telpo.server.trip;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface TripRepository extends JpaRepository<Trip, Long> {

    Optional<Trip> findByEditTokenHash(String editTokenHash);

    Optional<Trip> findByShareToken(String shareToken);
}