package walletmanager.entity;

import walletmanager.exception.UserValidationException;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
public class UserEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    public UserEntity(String name)
    {
        if (name == null || name.isBlank())
        {
            throw new UserValidationException("User name must not be empty");
        }

        this.name = name;
    }
}