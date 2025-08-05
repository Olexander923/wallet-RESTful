package shadrin.dev.wallet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {

    @Id
    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "balance", nullable = false)
    private BigDecimal amount;

    @Version
    @Min(0)
    @Column(name = "version", nullable = false)
    private Long version;


    public Wallet(UUID walletId, BigDecimal initialAmount) {
        this.walletId = Objects.requireNonNull(walletId, "Wallet ID cannot be null");
        this.amount = (initialAmount != null && initialAmount.compareTo(BigDecimal.ZERO) >= 0) ?
                initialAmount : BigDecimal.ZERO;

    }

    public UUID getWalletId() {
        return walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getVersion() {
        return version;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(walletId, wallet.walletId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId);
    }
}

