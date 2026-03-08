package walletmanager.entity;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import walletmanager.exception.AccountValidationException;
import walletmanager.exception.IllegalTransactionException;
import walletmanager.exception.InsufficientFundsException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static walletmanager.utils.TestConstants.*;

public class AccountEntityTest
{
    @Test
    public void returnsException_whenParameterInvalid()
    {
        assertThrows(AccountValidationException.class, () -> new AccountEntity(null, BigDecimal.TEN, user()));
        assertThrows(AccountValidationException.class, () -> new AccountEntity(PLN, BigDecimal.valueOf(-10L), user()));
        assertThrows(AccountValidationException.class, () -> new AccountEntity(PLN, BigDecimal.TEN, null));
    }

    @Nested
    public class Withdraw
    {
        @Test
        public void updatesBalance_whenWithdrawLessThanBalance()
        {
            //GIVEN
            AccountEntity account = account();

            //WHEN
            account.withdraw(BigDecimal.valueOf(50L));
            account.withdraw(BigDecimal.valueOf(30L));

            //THEN
            assertEquals(BigDecimal.valueOf(20L), account.getBalance());
            assertEquals(PLN, account.getCurrency());
        }

        @Test
        public void throwsInsufficientFunds_whenWithdrawMoreThanBalance()
        {
            assertThrows(InsufficientFundsException.class, () -> account().withdraw(BigDecimal.valueOf(110L)));
        }

        @Test
        public void throwsIllegalTransaction_whenWithdrawNegativeAmount()
        {
            assertThrows(IllegalTransactionException.class, () -> account().withdraw(BigDecimal.valueOf(-10L)));
        }
    }

    @Nested
    class Deposit
    {
        @Test
        public void updatesBalance_whenDepositPositiveAmount()
        {
            //GIVEN
            AccountEntity account = account();

            //WHEN
            account.deposit(BigDecimal.valueOf(50L));
            account.deposit(BigDecimal.valueOf(30L));

            //THEN
            assertEquals(BigDecimal.valueOf(180), account.getBalance());
            assertEquals(PLN, account.getCurrency());
        }
    }

    @Test
    public void throwsIllegalTransaction_whenDepositNegativeAmount()
    {
        assertThrows(IllegalTransactionException.class, () -> account().deposit(BigDecimal.valueOf(-10L)));
    }
}