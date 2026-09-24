package com.telpo.server.trip;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trip")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "edit_token_hash", nullable = false, unique = true)
    private String editTokenHash;

    @Column(name = "share_token", nullable = false, unique = true)
    private String shareToken;

    /** 추후 로그인 기능 추가 시 사용. 현재 단계에서는 항상 null. */
    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Setter
    @Column(nullable = false)
    private String title;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Setter
    @Column(name = "start_date")
    private LocalDate startDate;

    @Setter
    @Column(name = "end_date")
    private LocalDate endDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Setter
    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    Trip(String editTokenHash, String shareToken, String title, String countryCode,
         LocalDate startDate, LocalDate endDate) {
        this.editTokenHash = editTokenHash;
        this.shareToken = shareToken;
        this.title = title;
        this.countryCode = countryCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastAccessedAt = Instant.now();
    }
}