package com.telpo.server.place;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 자체 장소 모델. 외부(카카오 등) 장소 ID는 참조(provider/providerPlaceId)로만 갖고
 * PK로 쓰지 않는다 — 해외 프로바이더와 혼합 가능하게 하기 위함 (명세서 6장).
 */
@Entity
@Table(name = "place", uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_place_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_place_id", nullable = false)
    private String providerPlaceId;

    public Place(String name, String address, double lat, double lng,
                 String countryCode, String provider, String providerPlaceId) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.countryCode = countryCode;
        this.provider = provider;
        this.providerPlaceId = providerPlaceId;
    }
}