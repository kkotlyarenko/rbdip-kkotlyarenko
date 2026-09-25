package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.product.ProductRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * God-класс: валидация, расчёт цены, персистентность и "уведомление
 * клиента" смешаны в одном методе. Цель для рефакторинга по SRP в ЛР1.
 */
@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PricingCalculator pricingCalculator;
    private final OrderNotifier orderNotifier;

    public OrderService(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PricingCalculator pricingCalculator,
            OrderNotifier orderNotifier) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.pricingCalculator = pricingCalculator;
        this.orderNotifier = orderNotifier;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        ValidatedOrder validated = OrderRequestValidator.validate(request);

        List<Product> products = new ArrayList<>();
        List<PricingCalculator.LineItem> lineItems = new ArrayList<>();
        for (ValidatedOrder.Item item : validated.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new IllegalArgumentException("product " + item.productId() + " not found"));
            products.add(product);
            lineItems.add(new PricingCalculator.LineItem(product.getPrice(), item.quantity()));
        }

        BigDecimal total = pricingCalculator.calculateOrderTotal(
                lineItems, validated.customerType(), validated.couponCode());

        Order order = orderRepository.save(OrderAssembler.newOrder(validated));

        for (int i = 0; i < products.size(); i++) {
            orderItemRepository.save(
                    OrderAssembler.newOrderItem(order.getId(), products.get(i), lineItems.get(i).quantity()));
        }

        orderNotifier.sendConfirmation(validated.customerFullName(), order.getId(), total);

        return order;
    }
}
