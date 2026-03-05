package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import user.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>
{
}
