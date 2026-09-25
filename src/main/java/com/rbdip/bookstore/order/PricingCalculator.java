package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Модуль расчёта цены заказа. Намеренно почти не покрыт тестами и
 * содержит magic numbers / нечитаемые ветвления скидок - цель для
 * характеризационных тестов (ЛР2) и mutation-testing гейта PIT (ЛР5).
 */
@Component
public class PricingCalculator {

    private static final int BULK_QUANTITY_THRESHOLD = 10;
    private static final BigDecimal BULK_DISCOUNT = BigDecimal.valueOf(0.95);
    private static final BigDecimal VIP_DISCOUNT = BigDecimal.valueOf(0.9);
    private static final BigDecimal WHOLESALE_DISCOUNT = BigDecimal.valueOf(0.85);
    private static final BigDecimal SAVE10_AMOUNT = BigDecimal.valueOf(10);
    private static final BigDecimal SAVE20_DISCOUNT = BigDecimal.valueOf(0.8);
    private static final BigDecimal LARGE_ORDER_THRESHOLD = BigDecimal.valueOf(1000);
    private static final BigDecimal LARGE_ORDER_DISCOUNT = BigDecimal.valueOf(0.98);
    private static final int SCALE = 2;

    public record LineItem(BigDecimal price, int quantity) {
    }

    public BigDecimal calculateOrderTotal(List<LineItem> items, String customerType, String couponCode) {
        BigDecimal total = BigDecimal.ZERO;

        for (LineItem item : items) {
            total = total.add(lineTotal(item));
        }

        total = applyCustomerDiscount(total, customerType);
        total = applyCoupon(total, couponCode);

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        if (total.compareTo(LARGE_ORDER_THRESHOLD) > 0) {
            total = total.multiply(LARGE_ORDER_DISCOUNT);
        }

        return total.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal lineTotal(LineItem item) {
        BigDecimal linePrice = item.price().multiply(BigDecimal.valueOf(item.quantity()));
        if (item.quantity() > BULK_QUANTITY_THRESHOLD) {
            linePrice = linePrice.multiply(BULK_DISCOUNT);
        }
        return linePrice;
    }

    private BigDecimal applyCustomerDiscount(BigDecimal total, String customerType) {
        if ("vip".equals(customerType)) {
            return total.multiply(VIP_DISCOUNT);
        }
        if ("wholesale".equals(customerType)) {
            return total.multiply(WHOLESALE_DISCOUNT);
        }
        return total;
    }

    private BigDecimal applyCoupon(BigDecimal total, String couponCode) {
        if ("SAVE10".equals(couponCode)) {
            return total.subtract(SAVE10_AMOUNT);
        }
        if ("SAVE20PERCENT".equals(couponCode)) {
            return total.multiply(SAVE20_DISCOUNT);
        }
        return total;
    }
}
