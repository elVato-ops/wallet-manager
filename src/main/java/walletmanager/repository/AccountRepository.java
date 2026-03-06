package walletmanager.repository;

import walletmanager.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountEntity, Long>
{
    List<AccountEntity> findByUserId(Long id);
}