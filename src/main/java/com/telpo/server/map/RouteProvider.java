package com.telpo.server.map;

import java.util.List;

import com.telpo.server.common.LatLng;

/**
 * 경로 계산 프로바이더 추상화. 국내는 Tmap, 해외 확장 시 Google 등 다른 구현체로 교체/라우팅한다.
 */
public interface RouteProvider {

    Route getRoute(List<LatLng> waypoints, TravelMode mode);

    DistanceMatrix getMatrix(List<LatLng> points);
}
