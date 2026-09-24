package com.telpo.server.route;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.telpo.server.map.DistanceMatrix;

/**
 * 소규모(10개 안팎) TSP 휴리스틱: 최근접 이웃으로 초기해를 만들고 2-opt로 개선한다
 * (명세서 9장). 정확한 최적해는 아니지만 이 규모에서는 충분하다.
 */
@Component
class RouteOptimizer {

    List<Integer> optimize(DistanceMatrix matrix) {
        int n = matrix.points().size();
        long[][] cost = matrix.durationsSeconds();

        List<Integer> order = nearestNeighbor(n, cost);
        twoOpt(order, cost);
        return order;
    }

    private List<Integer> nearestNeighbor(int n, long[][] cost) {
        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>(n);

        int current = 0;
        visited[0] = true;
        order.add(0);

        for (int step = 1; step < n; step++) {
            int next = -1;
            long best = Long.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && cost[current][j] < best) {
                    best = cost[current][j];
                    next = j;
                }
            }
            visited[next] = true;
            order.add(next);
            current = next;
        }
        return order;
    }

    private void twoOpt(List<Integer> order, long[][] cost) {
        int n = order.size();
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < n - 1; i++) {
                for (int k = i + 1; k < n; k++) {
                    if (routeLength(order, cost, i, k) < 0) {
                        java.util.Collections.reverse(order.subList(i, k + 1));
                        improved = true;
                    }
                }
            }
        }
    }

    /** i~k 구간을 뒤집었을 때의 비용 변화(음수면 개선). 경계 밖 구간은 비교 대상에서 제외한다. */
    private long routeLength(List<Integer> order, long[][] cost, int i, int k) {
        int n = order.size();
        int prev = i == 0 ? -1 : order.get(i - 1);
        int next = k == n - 1 ? -1 : order.get(k + 1);

        int a = order.get(i);
        int b = order.get(k);

        long before = 0;
        long after = 0;
        if (prev != -1) {
            before += cost[prev][a];
            after += cost[prev][b];
        }
        if (next != -1) {
            before += cost[b][next];
            after += cost[a][next];
        }
        return after - before;
    }
}
