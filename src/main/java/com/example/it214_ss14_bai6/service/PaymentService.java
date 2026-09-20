package com.example.it214_ss14_bai6.service;

import com.example.it214_ss14_bai6.entity.PaymentTransaction;
import com.example.it214_ss14_bai6.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository paymentRepository;

    @Transactional
    public void processPayment(String orderId, Double amount, String idempotencyKey) {
        Optional<PaymentTransaction> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            log.info("[PAYMENT_SERVICE] Giao dịch đã tồn tại (Lũy đẳng). Key: {}", idempotencyKey);
            return;
        }

        log.info("[PAYMENT_SERVICE] Trừ tiền thành công. Key: {}", idempotencyKey);
        PaymentTransaction tx = new PaymentTransaction();
        tx.setOrderId(orderId);
        tx.setAmount(amount);
        tx.setIdempotencyKey(idempotencyKey);
        tx.setStatus("SUCCESS");
        paymentRepository.save(tx);
    }

    @Transactional
    public void refundPayment(String orderId, String idempotencyKey) {
        log.info("[PAYMENT_SERVICE] Giao dịch bù: Đang hoàn tiền cho đơn hàng {}...", orderId);
        Optional<PaymentTransaction> tx = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (tx.isPresent() && "SUCCESS".equals(tx.get().getStatus())) {
            tx.get().setStatus("REFUNDED");
            paymentRepository.save(tx.get());
            log.info("[PAYMENT_SERVICE] Hoàn tiền thành công.");
        } else {
            log.info("[PAYMENT_SERVICE] Không tìm thấy giao dịch hoặc đã hoàn tiền.");
        }
    }
}
