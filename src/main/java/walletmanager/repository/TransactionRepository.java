package walletmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import walletmanager.entity.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long>
{
}
