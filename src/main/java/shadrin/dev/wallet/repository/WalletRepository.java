package shadrin.dev.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shadrin.dev.wallet.entity.Wallet;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface WalletRepository extends JpaRepository<Wallet,UUID> {
    Optional<Wallet> findById(UUID id);

}
