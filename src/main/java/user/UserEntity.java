package user;

import exception.UserValidationException;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
public class UserEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;

    public UserEntity(String name)
    {
        if (name == null)
        {
            throw new UserValidationException("User name must not be null");
        }

        this.name = name;
    }
}