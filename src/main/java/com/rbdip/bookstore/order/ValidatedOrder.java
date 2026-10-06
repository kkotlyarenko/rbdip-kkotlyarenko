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

    public String firstName() {
        String full = customerFullName == null ? "" : customerFullName.trim();
        int separator = full.indexOf(' ');
        return separator < 0 ? full : full.substring(0, separator);
    }

    public String lastName() {
        String full = customerFullName == null ? "" : customerFullName.trim();
        int separator = full.indexOf(' ');
        return separator < 0 ? "" : full.substring(separator + 1).trim();
    }
}
