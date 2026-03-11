package walletmanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import walletmanager.entity.TransactionEntity;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long>
{
    @Query("""
                SELECT t
                FROM TransactionEntity t
                WHERE t.toAccount.id = :accountId
                OR t.fromAccount.id = :accountId
        """)

    Page<TransactionEntity> findTransactionsForAccount(Long accountId, Pageable pageable);
}