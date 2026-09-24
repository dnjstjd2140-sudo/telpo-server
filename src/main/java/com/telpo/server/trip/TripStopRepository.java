package com.telpo.server.trip;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface TripStopRepository extends JpaRepository<TripStop, Long> {

    List<TripStop> findByTripIdOrderByDayNoAscOrderNoAsc(Long tripId);

    void deleteByTripId(Long tripId);
}