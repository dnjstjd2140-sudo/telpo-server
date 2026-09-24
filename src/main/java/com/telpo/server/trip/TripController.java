package com.telpo.server.trip;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.telpo.server.trip.dto.CreateTripRequest;
import com.telpo.server.trip.dto.CreateTripResponse;
import com.telpo.server.trip.dto.TripDetailResponse;
import com.telpo.server.trip.dto.UpdateTripRequest;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<CreateTripResponse> create(@Valid @RequestBody CreateTripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(request));
    }

    @GetMapping("/edit/{editToken}")
    public TripDetailResponse getForEdit(@PathVariable String editToken) {
        return tripService.getByEditToken(editToken);
    }

    @GetMapping("/share/{shareToken}")
    public TripDetailResponse getForShare(@PathVariable String shareToken) {
        return tripService.getByShareToken(shareToken);
    }

    @PutMapping("/edit/{editToken}")
    public TripDetailResponse update(@PathVariable String editToken, @Valid @RequestBody UpdateTripRequest request) {
        return tripService.update(editToken, request);
    }
}
