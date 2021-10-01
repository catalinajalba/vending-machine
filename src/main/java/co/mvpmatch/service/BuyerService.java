package co.mvpmatch.service;

import co.mvpmatch.domain.User;
import co.mvpmatch.repository.ProductRepository;
import co.mvpmatch.repository.UserRepository;
import co.mvpmatch.service.dto.BuyResponse;
import co.mvpmatch.web.rest.errors.BadRequestAlertException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

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
                    throw new BadRequestAlertException("Not enough money.","","");
                }

                if (product.getAmountAvailable() < quantity) {
                    throw new BadRequestAlertException("Amount unavailable","","");
                }

                List<Integer> coinTypesOrderedDesc = buyer.getDeposit().keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                change(coinTypesOrderedDesc, buyer.getDeposit(), coinTypesOrderedDesc.get(0), toIntExact(totalCost));

//                User seller = userRepository.findById(product.getSeller().getId()).get();
//                addMoney(seller.getDeposit(), totalCost);

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
        if (!buyer.getDeposit().containsKey(coin)) {
            throw new BadRequestAlertException("Wrong coin. Please add only coins of 5, 10, 20, 50, 100 cents.","","");
        }
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



    public static void change(Map<Integer, Integer> deposit, Integer value) {

        // precondition - value <= total funds
        List<Integer> coinTypesOrderedDesc = deposit.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        change(coinTypesOrderedDesc, deposit, coinTypesOrderedDesc.get(0), value);
    }

    public static void change(List<Integer> coinTypesOrderedDesc, Map<Integer, Integer> deposit, int coin, int value) {
        int neededCoins = value/coin;
        int coinsIHave = deposit.get(coin);

        if (coinsIHave > neededCoins) {
            value -= neededCoins * coin;
            takeCoins(deposit, coin, neededCoins);
            int lessValuableCoinsTotal = sumMinorCoins(deposit, coin, coinTypesOrderedDesc);
            if (lessValuableCoinsTotal < value) {
                takeCoins(deposit, coin, 1);
                takeMinorCoins(deposit, coin, coinTypesOrderedDesc);
                giveBackChange(deposit, coin, coinTypesOrderedDesc, (coin + lessValuableCoinsTotal) - value);
            } else {
                change(coinTypesOrderedDesc, deposit, coinTypesOrderedDesc.get(coinTypesOrderedDesc.indexOf(coin) + 1), value);
            }
        } else {
            value -= coinsIHave * coin;
            takeCoins(deposit, coin, coinsIHave);
            if (value > 0) {
                change(coinTypesOrderedDesc, deposit, coinTypesOrderedDesc.get(coinTypesOrderedDesc.indexOf(coin) + 1), value);
            }
            // else stop recursive calls and exit
        }
    }

    private static void takeCoins(Map<Integer, Integer> deposit, int coin, int neededCoins) {
        if (deposit.get(coin) < neededCoins) {
            throw new RuntimeException("Not enough coins.");
        }
        int remainedNrOfCoins = deposit.get(coin) - neededCoins;
        deposit.put(coin, remainedNrOfCoins);
    }

    private static void giveCoins(Map<Integer, Integer> deposit, int coin, int nr) {
        int updatedNrOfCoins = deposit.get(coin) + nr;
        deposit.put(coin, updatedNrOfCoins);
    }

    public static int sumMinorCoins(Map<Integer, Integer> deposit, int coin, List<Integer> coinTypesOrderedDesc) {
        int sum = 0;

        ListIterator<Integer> index = coinTypesOrderedDesc.listIterator();
        while (index.hasNext()) {
            int coinType = index.next();
            if (coinType == coin) {
                while (index.hasNext()) {
                    coinType = index.next();
                    sum += deposit.get(coinType) * coinType;
                }
                break;
            }
        }

        return sum;
    }

    public static void takeMinorCoins(Map<Integer, Integer> deposit, int coin, List<Integer> coinTypesOrderedDesc) {
        ListIterator<Integer> index = coinTypesOrderedDesc.listIterator();
        while (index.hasNext()) {
            int coinType = index.next();
            if (coinType == coin) {
                while(index.hasNext()) {
                    coinType = index.next();
                    deposit.put(coinType, 0);
                }
                return;
            }
        }
    }

    private static void giveBackChange(Map<Integer, Integer> deposit, int coin, List<Integer> coinTypesOrderedDesc, int value) {
        ListIterator<Integer> index = coinTypesOrderedDesc.listIterator();
        while (index.hasNext()) {
            int coinType = index.next();
            if (coinType == coin) {
                while((value > 0) && index.hasNext()) {
                    coinType = index.next();
                    int neededCoins = value/coinType;
                    giveCoins(deposit, coinType, neededCoins);
                    value -= neededCoins * coinType;
                }
                return;
            }
        }
    }

    private void addMoney(Map<Integer, Integer> deposit, Integer coin, Integer nr) {
        deposit.put(coin, deposit.get(coin) + nr);
    }
}
