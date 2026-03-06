package walletmanager.entity;

import lombok.NoArgsConstructor;
import walletmanager.exception.UserValidationException;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "user")
    List<AccountEntity> accounts = new ArrayList<>();

    public UserEntity(String name)
    {
        if (name == null || name.isBlank())
        {
            throw new UserValidationException("User name must not be empty");
        }

        this.name = name;
    }
}