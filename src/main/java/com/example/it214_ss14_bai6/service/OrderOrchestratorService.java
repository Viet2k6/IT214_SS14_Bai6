package com.example.it214_ss14_bai6.service;

import com.example.it214_ss14_bai6.dto.OrderRequest;
import com.example.it214_ss14_bai6.entity.Order;
import com.example.it214_ss14_bai6.enums.OrderEvent;
import com.example.it214_ss14_bai6.enums.OrderState;
import com.example.it214_ss14_bai6.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderOrchestratorService {

    private final OrderRepository orderRepository;
    private final StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;
    private final CinemaService cinemaService;

    @Transactional
    public Order createOrder(OrderRequest request) {
        Order order = new Order(
                request.getOrderId(),
                request.getAmount(),
                request.getSeatId(),
                request.getIdempotencyKey(),
                OrderState.PENDING.name()
        );
        orderRepository.save(order);

        StateMachine<OrderState, OrderEvent> sm = build(request.getOrderId());
        
        Message<OrderEvent> paymentMsg = MessageBuilder.withPayload(OrderEvent.START_PAYMENT)
                .setHeader("orderId", request.getOrderId())
                .setHeader("amount", request.getAmount())
                .setHeader("idempotencyKey", request.getIdempotencyKey())
                .setHeader("seatId", request.getSeatId())
                .build();
        
        sm.sendEvent(paymentMsg);
        
        if (sm.getState().getId() == OrderState.PAID) {
            Message<OrderEvent> reserveMsg = MessageBuilder.withPayload(OrderEvent.RESERVE_SEAT)
                    .setHeader("orderId", request.getOrderId())
                    .setHeader("amount", request.getAmount())
                    .setHeader("idempotencyKey", request.getIdempotencyKey())
                    .setHeader("seatId", request.getSeatId())
                    .build();
            sm.sendEvent(reserveMsg);
        }
        
        OrderState finalState = sm.getState().getId();
        order.setStatus(finalState.name());
        
        if (finalState == OrderState.SUCCESS) {
            cinemaService.confirmSeat(request.getSeatId());
            log.info("[SYSTEM] Trạng thái cuối: ORDER_SUCCESS. Giao dịch hoàn tất thành công.");
        } else if (finalState == OrderState.CANCELLED) {
            log.info("[SYSTEM] Trạng thái cuối: ORDER_CANCELLED. Dữ liệu nhất quán.");
        }
        
        orderRepository.save(order);
        return order;
    }

    private StateMachine<OrderState, OrderEvent> build(String orderId) {
        StateMachine<OrderState, OrderEvent> sm = stateMachineFactory.getStateMachine(orderId);
        sm.start();
        return sm;
    }
}
