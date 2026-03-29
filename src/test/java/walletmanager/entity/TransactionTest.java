package walletmanager.entity;

import org.junit.jupiter.api.Test;
import walletmanager.exception.TransactionValidationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static walletmanager.utils.TestConstants.*;

public class TransactionTest
{
    @Test
    public void throwsTransactionValidation_whenInputInvalid()
    {
        assertThrows(TransactionValidationException.class, () -> new Transaction(BigDecimal.ZERO, PLN, account(), account()));
        assertThrows(TransactionValidationException.class, () -> new Transaction(BigDecimal.valueOf(-50), PLN, account(), account()));
        assertThrows(TransactionValidationException.class, () -> new Transaction(TRANSFER_AMOUNT, null, account(), account()));
        assertThrows(TransactionValidationException.class, () -> new Transaction(TRANSFER_AMOUNT, PLN, null, account()));
        assertThrows(TransactionValidationException.class, () -> new Transaction(TRANSFER_AMOUNT, PLN, account(), null));
    }
}
