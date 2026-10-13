package com.rbdip.bookstore.order;

import com.rbdip.bookstore.purchase.PurchaseVerifier;
import org.springframework.stereotype.Component;

@Component
public class OrderPurchaseVerifier implements PurchaseVerifier {

    private final OrderItemRepository orderItemRepository;

    public OrderPurchaseVerifier(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public boolean hasPurchased(Long productId) {
        return orderItemRepository.existsByProductId(productId);
    }
}
