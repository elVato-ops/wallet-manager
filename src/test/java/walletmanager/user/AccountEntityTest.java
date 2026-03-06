package walletmanager.user;

import org.junit.jupiter.api.Test;
import walletmanager.entity.AccountEntity;
import walletmanager.entity.UserEntity;
import walletmanager.exception.AccountValidationException;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountEntityTest
{
    private static final Currency PLN = Currency.getInstance("PLN");

    @Test
    public void returnsException_whenParameterInvalid()
    {
        assertThrows(AccountValidationException.class, () -> new AccountEntity(null, BigDecimal.TEN, new UserEntity("Bobek")));
        assertThrows(AccountValidationException.class, () -> new AccountEntity(PLN, BigDecimal.valueOf(-10L), new UserEntity("Bobek")));
        assertThrows(AccountValidationException.class, () -> new AccountEntity(PLN, BigDecimal.TEN, null));
    }
}
