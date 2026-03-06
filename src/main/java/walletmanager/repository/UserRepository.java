package walletmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import walletmanager.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>
{
}
