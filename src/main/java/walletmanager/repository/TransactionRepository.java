package walletmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import walletmanager.transaction.TransactionEntity;

public interface TransactionRepository extends JpaRepository<Long, TransactionEntity>
{
}
