package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingOrderNotifier implements OrderNotifier {

    private static final Logger log = LoggerFactory.getLogger(LoggingOrderNotifier.class);

    @Override
    public void sendConfirmation(String customerName, Long orderId, BigDecimal total) {
        // Реальный почтовый транспорт не настроен в учебном проекте - здесь
        // просто эмулируется побочный эффект отправки письма.
        log.info("[email] Dear {}, your order #{} for {} has been placed.", customerName, orderId, total);
    }
}
