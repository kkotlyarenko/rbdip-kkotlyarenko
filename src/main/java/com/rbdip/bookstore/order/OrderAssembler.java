package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;

public final class OrderAssembler {

    private static final String INITIAL_STATUS = "new";

    private OrderAssembler() {
    }

    public static Order newOrder(ValidatedOrder request) {
        return new Order(
                request.customerFullName(), request.customerAddress(), request.customerPhone(), INITIAL_STATUS);
    }

    public static OrderItem newOrderItem(Long orderId, Product product, int quantity) {
        return new OrderItem(orderId, product.getName(), product.getPrice(), quantity);
    }
}
