package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.order.PricingCalculator.LineItem;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PricingCalculatorCharacterizationTest {

    private final PricingCalculator calculator = new PricingCalculator();

    @Test
    void sumsLinesWithoutDiscounts() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 2), new LineItem(new BigDecimal("5.50"), 4)),
                "regular",
                null);

        assertThat(total).isEqualTo(new BigDecimal("42.00"));
    }

    @Test
    void emptyItemsProduceZero() {
        BigDecimal total = calculator.calculateOrderTotal(List.of(), "regular", null);

        assertThat(total).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void quantityAtBulkThresholdIsNotDiscounted() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 10)), "regular", null);

        assertThat(total).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    void quantityAboveBulkThresholdGetsFivePercentOff() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 11)), "regular", null);

        assertThat(total).isEqualTo(new BigDecimal("104.50"));
    }

    @Test
    void vipCustomerGetsTenPercentOff() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "vip", null);

        assertThat(total).isEqualTo(new BigDecimal("90.00"));
    }

    @Test
    void wholesaleCustomerGetsFifteenPercentOff() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "wholesale", null);

        assertThat(total).isEqualTo(new BigDecimal("85.00"));
    }

    @Test
    void unknownCustomerTypeIsIgnored() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "gold", null);

        assertThat(total).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    void save10CouponSubtractsTen() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "regular", "SAVE10");

        assertThat(total).isEqualTo(new BigDecimal("90.00"));
    }

    @Test
    void save10CouponClampsNegativeTotalToZero() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("5.00"), 1)), "regular", "SAVE10");

        assertThat(total).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void save20PercentCouponTakesTwentyPercentOff() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "regular", "SAVE20PERCENT");

        assertThat(total).isEqualTo(new BigDecimal("80.00"));
    }

    @Test
    void unknownCouponIsIgnored() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "regular", "BOGUS");

        assertThat(total).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    void totalAtLargeOrderThresholdIsNotDiscounted() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("1000.00"), 1)), "regular", null);

        assertThat(total).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void totalAboveLargeOrderThresholdGetsTwoPercentOff() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("1001.00"), 1)), "regular", null);

        assertThat(total).isEqualTo(new BigDecimal("980.98"));
    }

    @Test
    void discountsApplyInOrderBulkThenCustomerThenCouponThenLargeOrder() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("200.00"), 11)), "vip", "SAVE20PERCENT");

        assertThat(total).isEqualTo(new BigDecimal("1474.70"));
    }

    @Test
    void totalRoundsHalfUpToTwoDecimals() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.005"), 1)), "regular", null);

        assertThat(total).isEqualTo(new BigDecimal("10.01"));
    }
}
