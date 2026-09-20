package com.example.it214_ss14_bai6.config;

import com.example.it214_ss14_bai6.enums.OrderEvent;
import com.example.it214_ss14_bai6.enums.OrderState;
import com.example.it214_ss14_bai6.service.CinemaService;
import com.example.it214_ss14_bai6.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StateMachineActions {

    private final PaymentService paymentService;
    private final CinemaService cinemaService;

    @Bean
    public Action<OrderState, OrderEvent> paymentAction() {
        return context -> {
            String orderId = context.getMessageHeaders().get("orderId", String.class);
            Double amount = context.getMessageHeaders().get("amount", Double.class);
            String idempotencyKey = context.getMessageHeaders().get("idempotencyKey", String.class);
            
            log.info("[ORCHESTRATOR] Gửi lệnh trừ tiền đến Payment Service cho order: {}", orderId);
            
            try {
                paymentService.processPayment(orderId, amount, idempotencyKey);
                context.getStateMachine().sendEvent(OrderEvent.PAYMENT_SUCCESS);
            } catch (Exception e) {
                log.error("[ORCHESTRATOR] Lỗi khi thanh toán: {}", e.getMessage());
                context.getStateMachine().sendEvent(OrderEvent.COMPENSATE);
            }
        };
    }

    @Bean
    public Action<OrderState, OrderEvent> reserveSeatAction() {
        return context -> {
            String orderId = context.getMessageHeaders().get("orderId", String.class);
            String seatId = context.getMessageHeaders().get("seatId", String.class);
            
            log.info("[ORCHESTRATOR] Gửi lệnh giữ ghế {} sang Cinema Service", seatId);
            
            try {
                cinemaService.reserveSeat(seatId, orderId);
                context.getStateMachine().sendEvent(OrderEvent.RESERVE_SUCCESS);
            } catch (Exception e) {
                log.error("[CINEMA_SERVICE] LỖI: {}", e.getMessage());
                context.getStateMachine().sendEvent(OrderEvent.RESERVE_FAIL);
            }
        };
    }

    @Bean
    public Action<OrderState, OrderEvent> compensateAction() {
        return context -> {
            String orderId = context.getMessageHeaders().get("orderId", String.class);
            String idempotencyKey = context.getMessageHeaders().get("idempotencyKey", String.class);
            
            log.info("[ORCHESTRATOR] Phát hiện lỗi. Transition to CANCELLED. Kích hoạt Giao dịch bù.");
            
            try {
                paymentService.refundPayment(orderId, idempotencyKey);
            } catch (Exception e) {
                log.error("[PAYMENT_SERVICE] Lỗi khi hoàn tiền: {}", e.getMessage());
            }
        };
    }
}
