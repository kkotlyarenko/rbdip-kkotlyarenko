package com.rbdip.bookstore.order;

import java.util.List;

public record ValidatedOrder(
        String customerFullName,
        String customerAddress,
        String customerPhone,
        String customerType,
        String couponCode,
        List<Item> items) {

    public record Item(Long productId, int quantity) {
    }
}
