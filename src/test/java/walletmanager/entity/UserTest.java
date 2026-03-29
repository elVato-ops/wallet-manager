package walletmanager.entity;

import walletmanager.exception.UserValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest
{
    @Test
    public void throwsException_whenParameterInvalid()
    {
        assertThrows(UserValidationException.class, () -> new User(null));
        assertThrows(UserValidationException.class, () -> new User(""));
        assertThrows(UserValidationException.class, () -> new User("       "));
    }
}
