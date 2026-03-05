package user;

import exception.UserValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserEntityTest
{
    @Test
    public void constructorValidation_shouldThrowException()
    {
        assertThrows(UserValidationException.class, () -> new UserEntity(null));
    }
}
