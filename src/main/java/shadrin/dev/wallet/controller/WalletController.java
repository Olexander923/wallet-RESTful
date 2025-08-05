package shadrin.dev.wallet.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shadrin.dev.wallet.dto.WalletRequestDTO;
import shadrin.dev.wallet.operations.OperationType;
import shadrin.dev.wallet.service.WalletService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WalletController {
    private final WalletService walletService;


    @Autowired
    public WalletController(WalletService walletService
    ) {
        this.walletService = walletService;
    }

    /**
     * отображаем баланс кошелька по ID, в противном случае -
     * @return ошибка со статусом
     */
    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<?> checkBalance(@PathVariable UUID walletId) {
        try {
            BigDecimal balance = walletService.checkBalance(walletId);
            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (IllegalArgumentException ex) {
            // Лучше возвращать 404, если кошелек не найден
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            // Логирование ошибки ex.printStackTrace(); // Или использовать логгер
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error while checking balance"));
        }
    }


    /**
     * получает запрос на создание кошелька и
     * @return статус без тела при успехе, при ошибке 500 соответственно
     */
    @PostMapping("/wallets")
    public ResponseEntity<?> createWallet(@Valid @RequestBody WalletRequestDTO walletRequest) {
        try {
            UUID walletId = walletRequest.getWalletId();
            OperationType operationType = walletRequest.getOperationType();
            BigDecimal amount = walletRequest.getAmount();

            if (walletId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Wallet ID is required"));
            }

            if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Amount must be non-negative"));
            }

            switch (operationType) {
                case DEPOSIT:
                    walletService.deposit(walletId, amount);
                    return ResponseEntity.ok(Map.of("message", "Deposit successful"));

                case WITHDRAW:
                    walletService.withdraw(walletId, amount);
                    return ResponseEntity.ok(Map.of("message", "Withdrawal successful"));

                default:
                    return ResponseEntity.badRequest().body(Map.of("error", "Unsupported operation type: " + operationType));

            }

        } catch (IllegalArgumentException ex) {
            // Это охватывает случаи "кошелек не найден", "недостаточно средств", "некорректная сумма"
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            // Логирование ошибки ex.printStackTrace(); // Или использовать логгер
            // Проверим конкретную ошибку оптимистической блокировки
            if (ex.getCause() != null && ex.getCause().getMessage() != null &&
                    (ex.getCause().getMessage().contains("optimistic") || ex.getCause().getMessage().contains("version"))) {
                return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict
                        .body(Map.of("error", "Concurrent modification detected. Please retry the operation."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error during operation"));
        }

    }


}
