package shadrin.dev.wallet.dto;

import shadrin.dev.wallet.operations.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * слой DTO для запросов
 */
public class WalletRequestDTO {
    private UUID walletId;
    private OperationType operationType;
    private BigDecimal amount;

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public OperationType getOperationType() { return operationType; }
    public void setOperationType(OperationType operationType) { this.operationType = operationType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
