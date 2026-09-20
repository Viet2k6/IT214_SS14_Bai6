package com.example.it214_ss14_bai6.service;

import com.example.it214_ss14_bai6.entity.Seat;
import com.example.it214_ss14_bai6.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CinemaService {

    private final SeatRepository seatRepository;

    @Transactional
    public void reserveSeat(String seatId, String orderId) {
        Seat seat = seatRepository.findById(seatId).orElse(null);
        if (seat == null) {
            seat = new Seat(seatId, "AVAILABLE");
            seatRepository.save(seat);
        }

        if ("RESERVED".equals(seat.getStatus()) || "CONFIRMED".equals(seat.getStatus())) {
            throw new RuntimeException("Ghế " + seatId + " đã có người đặt!");
        }

        seat.setStatus("RESERVED");
        seatRepository.save(seat);
        log.info("[CINEMA_SERVICE] Đặt ghế {} thành công! (Trạng thái: Tạm giữ)", seatId);
    }
    
    @Transactional
    public void confirmSeat(String seatId) {
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new RuntimeException("Seat not found"));
        seat.setStatus("CONFIRMED");
        seatRepository.save(seat);
        log.info("[CINEMA_SERVICE] Đã xác nhận ghế {}!", seatId);
    }
}
