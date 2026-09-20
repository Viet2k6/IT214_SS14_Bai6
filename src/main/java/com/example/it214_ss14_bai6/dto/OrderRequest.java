package com.example.it214_ss14_bai6.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private String orderId;
    private double amount;
    private String seatId;
    private String idempotencyKey;
}
