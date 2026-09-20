package com.example.it214_ss14_bai6.controller;

import com.example.it214_ss14_bai6.dto.OrderRequest;
import com.example.it214_ss14_bai6.entity.Order;
import com.example.it214_ss14_bai6.service.OrderOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderOrchestratorService orchestratorService;

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        Order order = orchestratorService.createOrder(request);
        return ResponseEntity.ok(order);
    }
}
