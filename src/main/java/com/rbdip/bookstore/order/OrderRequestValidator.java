package com.rbdip.bookstore.order;

import java.util.ArrayList;
import java.util.List;

public final class OrderRequestValidator {

    private static final String DEFAULT_CUSTOMER_TYPE = "regular";

    private OrderRequestValidator() {
    }

    public static ValidatedOrder validate(CreateOrderRequest request) {
        requireText(request.customerFullName(), "customerFullName is required");
        requireText(request.customerAddress(), "customerAddress is required");
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("order must contain at least one item");
        }

        List<ValidatedOrder.Item> items = new ArrayList<>();
        for (CreateOrderRequest.Item raw : request.items()) {
            items.add(validateItem(raw));
        }

        String customerType = request.customerType() == null ? DEFAULT_CUSTOMER_TYPE : request.customerType();
        return new ValidatedOrder(
                request.customerFullName(),
                request.customerAddress(),
                request.customerPhone(),
                customerType,
                request.couponCode(),
                items);
    }

    private static ValidatedOrder.Item validateItem(CreateOrderRequest.Item raw) {
        if (raw == null) {
            throw new IllegalArgumentException("order item must not be null");
        }
        int quantity = raw.quantity() == null ? 1 : raw.quantity();
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        return new ValidatedOrder.Item(raw.productId(), quantity);
    }

    private static void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
