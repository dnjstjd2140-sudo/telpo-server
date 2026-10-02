package com.telpo.server.map;

import java.util.List;

import com.telpo.server.common.LatLng;

/**
 * 장소 검색 프로바이더 추상화. 카카오 등 특정 프로바이더의 응답 형식이 서비스 코드로 새지 않게 한다.
 * 경로 계산은 이동수단/지역별로 프로바이더가 달라질 수 있어 {@link RouteProvider}로 분리했다.
 */
public interface MapProvider {

    List<PlaceCandidate> searchPlaces(String keyword, LatLng near);
}
