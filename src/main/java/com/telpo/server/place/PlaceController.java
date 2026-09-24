package com.telpo.server.place;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telpo.server.common.LatLng;
import com.telpo.server.place.dto.PlaceSearchResult;

@RestController
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/api/places/search")
    public List<PlaceSearchResult> search(
            @RequestParam String q,
            @RequestParam double lat,
            @RequestParam double lng
    ) {
        return placeService.search(q, new LatLng(lat, lng));
    }
}