package com.rbdip.bookstore.order;

import com.rbdip.bookstore.customer.Customer;
import com.rbdip.bookstore.product.Product;

public final class OrderAssembler {

    private static final String INITIAL_STATUS = "new";

    private OrderAssembler() {
    }

    public static Customer newCustomer(ValidatedOrder request) {
        return new Customer(request.customerFullName(), request.customerAddress(), request.customerPhone());
    }

    public static Order newOrder(Customer customer) {
        return new Order(customer, INITIAL_STATUS);
    }

    public static OrderItem newOrderItem(Long orderId, Product product, int quantity) {
        return new OrderItem(orderId, product, quantity);
    }
}
