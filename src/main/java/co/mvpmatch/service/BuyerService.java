package co.mvpmatch.service;

import co.mvpmatch.domain.Product;
import co.mvpmatch.domain.User;
import co.mvpmatch.repository.ProductRepository;
import co.mvpmatch.repository.UserRepository;
import co.mvpmatch.service.dto.BuyResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.toIntExact;
import static java.util.Map.entry;

@Service
public class BuyerService {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    public BuyerService(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public BuyResponse buy(String userName, Long productId, Integer quantity) {
        final BuyResponse response = new BuyResponse();
        productRepository.findById(productId).ifPresent(
            product -> {
                Long totalCost = quantity * product.getCost();
                User buyer = userRepository.findOneByUsername(userName).get();
                if (buyer.getMoney() < totalCost) {
                    throw new RuntimeException("Not enough money.");
                }

                if (product.getAmountAvailable() < quantity) {
                    throw new RuntimeException("Amount unavailable");
                }

                substractMoney(buyer.getDeposit(), totalCost);

                User seller = userRepository.findById(product.getSeller().getId()).get();
                addMoney(seller.getDeposit(), totalCost);

                product.setAmountAvailable(product.getAmountAvailable() - quantity);

                response.setDeposit(buyer.getDeposit());
                response.setProduct(product);
                response.setTotalSpent(totalCost);
                response.setQuantity(quantity);
            }
        );
        return response;
    }

    public void deposit(String userName, Integer coin, Integer nr) {
        User buyer = userRepository.findOneByUsername(userName).get();
        addMoney(buyer.getDeposit(), coin, nr);
    }

    public Map<Integer, Integer> resetDeposit(String userName) {
        User buyer = userRepository.findOneByUsername(userName).get();
        Map<Integer, Integer> change = buyer.getDeposit();
        buyer.setDeposit(Map.ofEntries(
            entry(5, 0),
            entry(10, 0),
            entry(20, 0),
            entry(50, 0),
            entry(100, 0)
        ));
        return change;
    }

    private void substractMoney(Map<Integer, Integer> deposit, Long value) {
        Integer coins_100 = toIntExact(value/100);
        Integer have_100 = deposit.get(100);
        if (have_100 > coins_100) {
            deposit.put(100, deposit.get(100) - coins_100);
            value = value%100;
        } else {
            deposit.put(100, 0);
            value -= have_100 * 100;
        }

        Integer coins_50 = toIntExact(value/50);
        Integer have_50 = deposit.get(50);
        if (have_50 > coins_50) {
            deposit.put(50, deposit.get(50) - coins_50);
            value = value%50;
        } else {
            deposit.put(50, 0);
            value -= have_50 * 50;
        }

        Integer coins_20 = toIntExact(value/20);
        Integer have_20 = deposit.get(20);
        if (have_20 > coins_20) {
            deposit.put(20, deposit.get(20) - coins_20);
            value = value%20;
        } else {
            deposit.put(20, 0);
            value -= have_20 * 20;
        }

        Integer coins_10 = toIntExact(value/10);
        Integer have_10 = deposit.get(10);
        if (have_10 > coins_10) {
            deposit.put(10, deposit.get(10) - coins_10);
            value = value%10;
        } else {
            deposit.put(10, 0);
            value -= have_10 * 10;
        }

        Integer coins_5 = toIntExact(value/5);
        Integer have_5 = deposit.get(5);
        if (have_5 > coins_5) {
            deposit.put(5, deposit.get(5) - coins_5);
            value = value%5;
        } else {
            deposit.put(5, 0);
            value -= have_5 * 5;
        }
    }

    private void addMoney(Map<Integer, Integer> deposit, Long value) {
        Integer coins_100 = toIntExact(value/100);
        value = value%100;

        Integer coins_50 = toIntExact(value /50);
        value = value%50;

        Integer coins_20 = toIntExact(value /20);
        value = value%20;

        Integer coins_10 = toIntExact(value /10);
        value = value%10;

        Integer coins_5 = toIntExact(value /5);

        deposit.put(100, deposit.get(100) + coins_100);
        deposit.put(50, deposit.get(50) + coins_50);
        deposit.put(20, deposit.get(20) + coins_20);
        deposit.put(10, deposit.get(10) + coins_10);
        deposit.put(5, deposit.get(5) + coins_5);
    }

    private void addMoney(Map<Integer, Integer> deposit, Integer coin, Integer nr) {
        deposit.put(coin, deposit.get(coin) + nr);
    }
}
