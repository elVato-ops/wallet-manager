package walletmanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import walletmanager.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>
{
    Page<Account> findByUserId(Long id, Pageable pageable);
}