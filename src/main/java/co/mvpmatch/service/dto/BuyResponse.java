package co.mvpmatch.service.dto;

import co.mvpmatch.domain.Product;

import java.util.Map;

public class BuyResponse {

    private Long totalSpent;

    private Product product;

    private Map<Integer, Integer> deposit;

    private Integer quantity;

    public Long getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Long totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Map<Integer, Integer> getDeposit() {
        return deposit;
    }

    public void setDeposit(Map<Integer, Integer> deposit) {
        this.deposit = deposit;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
