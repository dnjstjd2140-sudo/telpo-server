package com.telpo.server.route;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.telpo.server.route.dto.OptimizeRequest;
import com.telpo.server.route.dto.OptimizeResponse;
import com.telpo.server.route.dto.RouteRequest;
import com.telpo.server.route.dto.RouteResponse;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public RouteResponse getRoute(@Valid @RequestBody RouteRequest request) {
        return RouteResponse.from(routeService.getRoute(request.waypoints(), request.mode()));
    }

    @PostMapping("/optimize")
    public OptimizeResponse optimize(@Valid @RequestBody OptimizeRequest request) {
        return routeService.optimize(request.points());
    }
}
