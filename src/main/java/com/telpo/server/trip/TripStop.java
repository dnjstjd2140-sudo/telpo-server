package com.telpo.server.trip;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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

    private String memo;

    @Column(name = "stay_minutes")
    private Integer stayMinutes;

    public TripStop(Trip trip, Place place, int dayNo, int orderNo, String memo, Integer stayMinutes) {
        this.trip = trip;
        this.place = place;
        this.dayNo = dayNo;
        this.orderNo = orderNo;
        this.memo = memo;
        this.stayMinutes = stayMinutes;
    }
}