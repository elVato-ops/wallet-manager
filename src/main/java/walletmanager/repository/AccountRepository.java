package walletmanager.repository;

import walletmanager.account.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Long, AccountEntity>
{
}
