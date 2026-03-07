package walletmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import walletmanager.exception.UserValidationException;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class UserEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
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