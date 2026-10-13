package com.rbdip.bookstore.purchase;

public interface PurchaseVerifier {

    boolean hasPurchased(Long productId);
}
