package com.telpo.server.place;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telpo.server.common.LatLng;
import com.telpo.server.map.MapProvider;
import com.telpo.server.place.dto.PlaceSearchResult;

@Service
public class PlaceService {

    private final MapProvider mapProvider;
    private final PlaceRepository placeRepository;

    public PlaceService(MapProvider mapProvider, PlaceRepository placeRepository) {
        this.mapProvider = mapProvider;
        this.placeRepository = placeRepository;
    }

    @Transactional(readOnly = true)
    public List<PlaceSearchResult> search(String keyword, LatLng near) {
        return mapProvider.searchPlaces(keyword, near).stream()
                .map(PlaceSearchResult::from)
                .toList();
    }

    /**
     * 검색 결과를 여행에 담을 때 자체 Place 엔티티로 변환/재사용한다.
     * 이미 저장된 (provider, providerPlaceId) 조합이면 기존 엔티티를 재사용한다.
     */
    @Transactional
    public Place resolveOrCreate(PlaceSearchResult candidate, String countryCode) {
        return placeRepository.findByProviderAndProviderPlaceId(candidate.provider(), candidate.providerPlaceId())
                .orElseGet(() -> placeRepository.save(new Place(
                        candidate.name(),
                        candidate.address(),
                        candidate.lat(),
                        candidate.lng(),
                        countryCode,
                        candidate.provider(),
                        candidate.providerPlaceId()
                )));
    }
}