package com.example.it214_ss14_bai6.config;

import com.example.it214_ss14_bai6.enums.OrderEvent;
import com.example.it214_ss14_bai6.enums.OrderState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderState, OrderEvent> {

    private final Action<OrderState, OrderEvent> paymentAction;
    private final Action<OrderState, OrderEvent> reserveSeatAction;
    private final Action<OrderState, OrderEvent> compensateAction;

    private final StateMachineRuntimePersister<OrderState, OrderEvent, String> stateMachineRuntimePersister;

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states.withStates()
                .initial(OrderState.PENDING)
                .states(EnumSet.allOf(OrderState.class))
                .end(OrderState.SUCCESS)
                .end(OrderState.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
                .withExternal().source(OrderState.PENDING).target(OrderState.PAYING).event(OrderEvent.START_PAYMENT)
                .action(paymentAction)
                .and()
                .withExternal().source(OrderState.PAYING).target(OrderState.PAID).event(OrderEvent.PAYMENT_SUCCESS)
                .and()
                .withExternal().source(OrderState.PAID).target(OrderState.RESERVING).event(OrderEvent.RESERVE_SEAT)
                .action(reserveSeatAction)
                .and()
                .withExternal().source(OrderState.RESERVING).target(OrderState.SUCCESS).event(OrderEvent.RESERVE_SUCCESS)
                .and()
                .withExternal().source(OrderState.RESERVING).target(OrderState.CANCELLED).event(OrderEvent.RESERVE_FAIL)
                .action(compensateAction)
                .and()
                .withExternal().source(OrderState.PAYING).target(OrderState.CANCELLED).event(OrderEvent.COMPENSATE)
                .action(compensateAction);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
        StateMachineListenerAdapter<OrderState, OrderEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
                log.info("[ORCHESTRATOR] State Change: {} -> {}", (from != null ? from.getId() : "none"), to.getId());
            }
        };
        config.withConfiguration().listener(adapter);
        config.withPersistence().runtimePersister(stateMachineRuntimePersister);
    }
}
