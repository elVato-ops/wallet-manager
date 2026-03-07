package walletmanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import walletmanager.entity.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Long>
{
    Page<AccountEntity> findByUserId(Long id, Pageable pageable);
}