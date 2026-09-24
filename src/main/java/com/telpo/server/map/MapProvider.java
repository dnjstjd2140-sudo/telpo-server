package com.telpo.server.map;

import java.util.List;

import com.telpo.server.common.LatLng;

/**
 * 지도/검색/경로 기능의 프로바이더 추상화. 비즈니스 로직은 이 인터페이스에만 의존하고,
 * 카카오 등 특정 프로바이더의 응답 형식이 서비스 코드로 새지 않게 한다.
 */
public interface MapProvider {

    List<PlaceCandidate> searchPlaces(String keyword, LatLng near);

    Route getRoute(List<LatLng> waypoints, TravelMode mode);

    DistanceMatrix getMatrix(List<LatLng> points);
}