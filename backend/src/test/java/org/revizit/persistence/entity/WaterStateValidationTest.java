package org.revizit.persistence.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WaterStateValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        WaterState state = new WaterState();
        state.setEmptyCnt(0);
        state.setFullCnt(10);
        state.setCurrPct(50);

        Set<ConstraintViolation<WaterState>> violations = validator.validate(state);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenEmptyCntNegative_thenViolations() {
        WaterState state = new WaterState();
        state.setEmptyCnt(-1);
        state.setFullCnt(10);
        state.setCurrPct(50);

        Set<ConstraintViolation<WaterState>> violations = validator.validate(state);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenFullCntNegative_thenViolations() {
        WaterState state = new WaterState();
        state.setEmptyCnt(0);
        state.setFullCnt(-1);
        state.setCurrPct(50);

        Set<ConstraintViolation<WaterState>> violations = validator.validate(state);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenCurrPctNegative_thenViolations() {
        WaterState state = new WaterState();
        state.setEmptyCnt(0);
        state.setFullCnt(10);
        state.setCurrPct(-1);

        Set<ConstraintViolation<WaterState>> violations = validator.validate(state);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenCurrPctOver100_thenViolations() {
        WaterState state = new WaterState();
        state.setEmptyCnt(0);
        state.setFullCnt(10);
        state.setCurrPct(101);

        Set<ConstraintViolation<WaterState>> violations = validator.validate(state);
        assertFalse(violations.isEmpty());
    }
}
