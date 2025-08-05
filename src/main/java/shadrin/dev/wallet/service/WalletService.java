package shadrin.dev.wallet.service;

import jakarta.transaction.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import shadrin.dev.wallet.entity.Wallet;
import shadrin.dev.wallet.repository.WalletRepository;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * сервис реализующий тип операций с кошельком
 */
@Service
public class WalletService {
    private final WalletRepository repository;


    public WalletService(WalletRepository walletRepository) {
        this.repository = walletRepository;

    }

    /**
     * пополнение баланса с раелизацией оптимист-блокировки(с количеством попыток и задержкой)
     * Если кошелек не существует, будет создан с указанным начальным балансом.
     * @throws IllegalArgumentException если сумма некорректна
     */
    @Transactional
    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class}, // Повторять при ошибке оптимистической блокировки
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2))
    public void deposit(UUID walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive. Received: " + amount);
        }

        Wallet wallet = repository.findById(walletId).orElse(null);

        if (wallet == null) {
            wallet = new Wallet(walletId, amount);
            repository.save(wallet);
            System.out.printf("Successfully created wallet! Wallet ID: %s, Initial balance: %.2f%n", walletId, amount);
        } else {
            // Если кошелек существует, пополняем баланс
            BigDecimal newBalance = wallet.getAmount().add(amount);
            wallet.setAmount(newBalance);
            repository.save(wallet);
            System.out.printf("Successfully deposited! Amount: %.2f, Wallet ID: %s, New balance: %.2f%n",
                    amount, walletId, newBalance);

        }
    }

    /**
     * вывод средств с раелизацией оптимист-блокировки(с количеством попыток и задержкой)
     * @throws IllegalArgumentException если сумма некорректна, кошелек не существует
     */
    @Transactional
    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2))
    public void withdraw(UUID walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive. Received: " + amount);
        }

        Wallet wallet = repository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet with ID " + walletId + " does not exist"));

        if (wallet.getAmount().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds. Available: " + wallet.getAmount() + ", Requested: " + amount);
        }

        BigDecimal newBalance = wallet.getAmount().subtract(amount);
        wallet.setAmount(newBalance);
        repository.save(wallet);
        System.out.printf("Successfully withdrawn! Amount: %.2f, Wallet ID: %s, New balance: %.2f%n",
                amount, walletId, newBalance);

    }


    /**
     * отображение баланса кошелька
     */
    @Transactional
    public BigDecimal checkBalance(UUID walletId) {
        var wallet = repository.findById(walletId).orElseThrow(() ->
                new IllegalArgumentException("No such wallet with ID=%s "
                        .formatted(walletId))
        );

        return wallet.getAmount();

    }


}
