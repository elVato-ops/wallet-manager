package walletmanager.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import walletmanager.entity.AccountEntity;
import walletmanager.entity.TransactionEntity;
import walletmanager.entity.UserEntity;
import walletmanager.request.TransferRequest;
import walletmanager.response.AccountResponse;
import walletmanager.response.TransactionResponse;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public class TestConstants
{
    public static final Long USER_ID = 17L;
    public static final String USER_NAME = "Bobek";
    public static final String SECOND_USER_NAME = "Chlebek";
    public static final String THIRD_USER_NAME = "Dudek";

    public static final Long ACCOUNT_ID = 997L;
    public static final Currency PLN = Currency.getInstance("PLN");
    public static final Currency EUR = Currency.getInstance("EUR");
    public static final BigDecimal BALANCE = BigDecimal.valueOf(100L);

    public static final Long TRANSACTION_ID = 10L;
    public static final Long OTHER_TRANSACTION_ID = 910L;
    public static final Long FROM_ACCOUNT_ID = 11L;
    public static final Long OTHER_FROM_ACCOUNT_ID = 911L;
    public static final Long TO_ACCOUNT_ID = 12L;
    public static final Long OTHER_TO_ACCOUNT_ID = 912L;
    public static final BigDecimal TRANSFER_AMOUNT = new BigDecimal(50);
    public static final BigDecimal OTHER_TRANSFER_AMOUNT = new BigDecimal(950);

    public static final Pageable PAGEABLE = PageRequest.of(0, 10);

    public static AccountEntity account()
    {
        return new AccountEntity(PLN, BALANCE, user());
    }

    public static AccountEntity otherAccount()
    {
        return new AccountEntity(PLN, BALANCE, otherUser());
    }

    public static AccountResponse accountResponse()
    {
        return new AccountResponse(ACCOUNT_ID, PLN, BALANCE, USER_ID);
    }

    public static UserEntity user()
    {
        return new UserEntity(USER_NAME);
    }

    public static UserEntity otherUser()
    {
        return new UserEntity(SECOND_USER_NAME);
    }

    public static Page<AccountEntity> accountPageEntity()
    {
        return new PageImpl<>(List.of(account()));
    }

    public static Page<AccountResponse> accountPageResponse()
    {
        return new PageImpl<>(List.of(accountResponse()));
    }

    public static TransactionEntity transactionEntity()
    {
        return new TransactionEntity(TRANSFER_AMOUNT, PLN, account(), otherAccount());
    }

    public static TransactionResponse transactionResponse()
    {
        return new TransactionResponse(TRANSACTION_ID, FROM_ACCOUNT_ID, TO_ACCOUNT_ID, PLN, TRANSFER_AMOUNT);
    }

    public static TransactionResponse otherTransactionResponse()
    {
        return new TransactionResponse(OTHER_TRANSACTION_ID, OTHER_FROM_ACCOUNT_ID, OTHER_TO_ACCOUNT_ID, EUR, OTHER_TRANSFER_AMOUNT);
    }

    public static TransferRequest transferRequest()
    {
        return new TransferRequest(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, TRANSFER_AMOUNT);
    }

    public static int toInt(Long value)
    {
        return Integer.parseInt(value.toString());
    }

    public static int toInt(BigDecimal value)
    {
        return Integer.parseInt(value.toString());
    }
}
