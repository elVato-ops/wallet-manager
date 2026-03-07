package walletmanager.user;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import walletmanager.entity.AccountEntity;
import walletmanager.entity.UserEntity;
import walletmanager.exception.AccountValidationException;
import walletmanager.exception.IllegalTransactionException;
import walletmanager.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountEntityTest
{
    private static final Currency PLN = Currency.getInstance("PLN");
    private static final UserEntity USER = new UserEntity("Bobek");

    @Test
    public void returnsException_whenParameterInvalid()
    {
        assertThrows(AccountValidationException.class, () -> new AccountEntity(null, BigDecimal.TEN, USER));
        assertThrows(AccountValidationException.class, () -> new AccountEntity(PLN, BigDecimal.valueOf(-10L), USER));
        assertThrows(AccountValidationException.class, () -> new AccountEntity(PLN, BigDecimal.TEN, null));
    }

    @Nested
    public class Withdraw
    {
        @Test
        public void updatesBalance_whenWithdrawLessThanBalance()
        {
            //GIVEN
            AccountEntity account = new AccountEntity(PLN, BigDecimal.valueOf(100L), USER);

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
            //GIVEN
            AccountEntity account = new AccountEntity(PLN, BigDecimal.valueOf(100L), USER);

            //THEN
            assertThrows(InsufficientFundsException.class, () -> account.withdraw(BigDecimal.valueOf(110L)));
        }

        @Test
        public void throwsIllegalTransaction_whenWithdrawNegativeAmount()
        {
            //GIVEN
            AccountEntity account = new AccountEntity(PLN, BigDecimal.valueOf(100L), USER);

            //THEN
            assertThrows(IllegalTransactionException.class, () -> account.withdraw(BigDecimal.valueOf(-10L)));
        }
    }

    @Nested
    class Deposit
    {
        @Test
        public void updatesBalance_whenDepositPositiveAmount()
        {
            //GIVEN
            AccountEntity account = new AccountEntity(PLN, BigDecimal.valueOf(100L), USER);

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
        //GIVEN
        AccountEntity account = new AccountEntity(PLN, BigDecimal.valueOf(100L), USER);

        //THEN
        assertThrows(IllegalTransactionException.class, () -> account.deposit(BigDecimal.valueOf(-10L)));
    }
}
