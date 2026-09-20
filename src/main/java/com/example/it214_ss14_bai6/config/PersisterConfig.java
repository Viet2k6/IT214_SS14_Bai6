package com.example.it214_ss14_bai6.config;

import com.example.it214_ss14_bai6.enums.OrderEvent;
import com.example.it214_ss14_bai6.enums.OrderState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Configuration
public class PersisterConfig {

    @Bean
    public StateMachineRuntimePersister<OrderState, OrderEvent, String> stateMachineRuntimePersister(
            JpaStateMachineRepository jpaStateMachineRepository) {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }
}
