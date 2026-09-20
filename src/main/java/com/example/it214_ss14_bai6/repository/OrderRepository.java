package com.example.it214_ss14_bai6.repository;

import com.example.it214_ss14_bai6.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
}
