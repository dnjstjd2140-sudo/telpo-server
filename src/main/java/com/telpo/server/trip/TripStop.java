package com.telpo.server.trip;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.telpo.server.map.TravelMode;
import com.telpo.server.place.Place;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trip_stop")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "day_no", nullable = false)
    private int dayNo;

    @Column(name = "order_no", nullable = false)
    private int orderNo;

    /** 이전 정류지에서 이 정류지로 이동할 때의 수단. 그날의 첫 정류지면 의미 없음(기본값만 채워짐). */
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'CAR'")
    @Column(name = "travel_mode", nullable = false)
    private TravelMode travelMode;

    private String memo;

    @Column(name = "stay_minutes")
    private Integer stayMinutes;

    public TripStop(Trip trip, Place place, int dayNo, int orderNo, TravelMode travelMode,
                     String memo, Integer stayMinutes) {
        this.trip = trip;
        this.place = place;
        this.dayNo = dayNo;
        this.orderNo = orderNo;
        this.travelMode = travelMode;
        this.memo = memo;
        this.stayMinutes = stayMinutes;
    }
}