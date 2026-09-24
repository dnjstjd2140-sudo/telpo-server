package com.telpo.server.route.dto;

import java.util.List;

/**
 * order[i] = 원래 points 배열에서의 인덱스. 방문 순서대로 나열된다.
 */
public record OptimizeResponse(
        List<Integer> order,
        long totalDurationSeconds
) {
}
