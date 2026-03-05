package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import transaction.TransactionEntity;

public interface TransactionRepository extends JpaRepository<Long, TransactionEntity>
{
}
