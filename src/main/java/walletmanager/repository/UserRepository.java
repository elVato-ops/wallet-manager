package walletmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import walletmanager.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
}
