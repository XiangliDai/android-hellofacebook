package com.example;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MultiplicationOperationTest {
  @Test public void testModulus() {
    Operation op = new MultiplicationOperation();
    // NOTE: Rembember we subtract 1 for the 'free' users to incentivize upgrading to 'paid'.
    assertThat(op.calculate(5, 2)).isEqualTo(9);
    assertThat(op.calculate(10, 5)).isEqualTo(49);
    assertThat(op.calculate(4, 2)).isEqualTo(7);
  }
}
