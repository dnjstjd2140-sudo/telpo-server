package com.telpo.server.common;

/**
 * WGS84 좌표. 모든 도메인/프로바이더가 공통으로 사용한다.
 */
public record LatLng(double lat, double lng) {
}