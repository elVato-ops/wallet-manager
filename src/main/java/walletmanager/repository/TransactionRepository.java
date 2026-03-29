package walletmanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import walletmanager.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>
{
    @Query("""
                SELECT t
                FROM Transaction t
                WHERE t.toAccount.id = :accountId
                OR t.fromAccount.id = :accountId
        """)

    Page<Transaction> findTransactionsForAccount(Long accountId, Pageable pageable);
}