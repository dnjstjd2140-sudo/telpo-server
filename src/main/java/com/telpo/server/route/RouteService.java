package com.telpo.server.route;

import java.util.List;

import org.springframework.stereotype.Service;

import com.telpo.server.common.LatLng;
import com.telpo.server.map.DistanceMatrix;
import com.telpo.server.map.MapProvider;
import com.telpo.server.map.Route;
import com.telpo.server.map.TravelMode;
import com.telpo.server.route.dto.OptimizeResponse;

@Service
public class RouteService {

    private final MapProvider mapProvider;
    private final RouteOptimizer routeOptimizer;

    public RouteService(MapProvider mapProvider, RouteOptimizer routeOptimizer) {
        this.mapProvider = mapProvider;
        this.routeOptimizer = routeOptimizer;
    }

    public Route getRoute(List<LatLng> waypoints, TravelMode mode) {
        return mapProvider.getRoute(waypoints, mode);
    }

    public OptimizeResponse optimize(List<LatLng> points) {
        DistanceMatrix matrix = mapProvider.getMatrix(points);
        List<Integer> order = routeOptimizer.optimize(matrix);

        long total = 0;
        for (int i = 0; i < order.size() - 1; i++) {
            total += matrix.durationsSeconds()[order.get(i)][order.get(i + 1)];
        }

        return new OptimizeResponse(order, total);
    }
}
