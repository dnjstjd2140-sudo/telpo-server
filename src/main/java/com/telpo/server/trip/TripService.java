package com.telpo.server.trip;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telpo.server.common.exception.NotFoundException;
import com.telpo.server.map.TravelMode;
import com.telpo.server.place.Place;
import com.telpo.server.place.PlaceService;
import com.telpo.server.place.dto.PlaceSearchResult;
import com.telpo.server.trip.dto.CreateTripRequest;
import com.telpo.server.trip.dto.CreateTripResponse;
import com.telpo.server.trip.dto.TripDetailResponse;
import com.telpo.server.trip.dto.UpdateTripRequest;
import com.telpo.server.trip.dto.UpdateTripStopRequest;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final TripStopRepository tripStopRepository;
    private final PlaceService placeService;

    public TripService(TripRepository tripRepository, TripStopRepository tripStopRepository, PlaceService placeService) {
        this.tripRepository = tripRepository;
        this.tripStopRepository = tripStopRepository;
        this.placeService = placeService;
    }

    @Transactional
    public CreateTripResponse createTrip(CreateTripRequest request) {
        String editToken = TokenGenerator.generate();
        String shareToken = TokenGenerator.generate();

        Trip trip = new Trip(
                TokenGenerator.hash(editToken),
                shareToken,
                request.title(),
                request.countryCode(),
                request.startDate(),
                request.endDate()
        );
        tripRepository.save(trip);

        return new CreateTripResponse(trip.getId(), editToken, shareToken);
    }

    @Transactional
    public TripDetailResponse getByEditToken(String editToken) {
        Trip trip = tripRepository.findByEditTokenHash(TokenGenerator.hash(editToken))
                .orElseThrow(() -> new NotFoundException("여행을 찾을 수 없습니다."));
        trip.setLastAccessedAt(Instant.now());
        return TripDetailResponse.of(trip, stopsOf(trip), false);
    }

    @Transactional
    public TripDetailResponse getByShareToken(String shareToken) {
        Trip trip = tripRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new NotFoundException("여행을 찾을 수 없습니다."));
        trip.setLastAccessedAt(Instant.now());
        return TripDetailResponse.of(trip, stopsOf(trip), true);
    }

    /**
     * 자동 저장: 여행 정보와 전체 일정을 통째로 받아 교체한다.
     */
    @Transactional
    public TripDetailResponse update(String editToken, UpdateTripRequest request) {
        Trip trip = tripRepository.findByEditTokenHash(TokenGenerator.hash(editToken))
                .orElseThrow(() -> new NotFoundException("여행을 찾을 수 없습니다."));

        trip.setTitle(request.title());
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        trip.setLastAccessedAt(Instant.now());

        tripStopRepository.deleteByTripId(trip.getId());
        for (UpdateTripStopRequest stopRequest : request.stops()) {
            Place place = placeService.resolveOrCreate(toSearchResult(stopRequest), trip.getCountryCode());
            tripStopRepository.save(new TripStop(
                    trip,
                    place,
                    stopRequest.dayNo(),
                    stopRequest.orderNo(),
                    stopRequest.travelMode() != null ? stopRequest.travelMode() : TravelMode.CAR,
                    stopRequest.memo(),
                    stopRequest.stayMinutes()
            ));
        }

        return TripDetailResponse.of(trip, stopsOf(trip), false);
    }

    private List<TripStop> stopsOf(Trip trip) {
        return tripStopRepository.findByTripIdOrderByDayNoAscOrderNoAsc(trip.getId());
    }

    private PlaceSearchResult toSearchResult(UpdateTripStopRequest stopRequest) {
        return new PlaceSearchResult(
                stopRequest.placeName(),
                stopRequest.placeAddress(),
                stopRequest.lat(),
                stopRequest.lng(),
                stopRequest.provider(),
                stopRequest.providerPlaceId()
        );
    }
}
