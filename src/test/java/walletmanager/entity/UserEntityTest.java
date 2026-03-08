package walletmanager.entity;

import walletmanager.exception.UserValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserEntityTest
{
    @Test
    public void throwsException_whenParameterInvalid()
    {
        assertThrows(UserValidationException.class, () -> new UserEntity(null));
        assertThrows(UserValidationException.class, () -> new UserEntity(""));
        assertThrows(UserValidationException.class, () -> new UserEntity("       "));
    }
}
