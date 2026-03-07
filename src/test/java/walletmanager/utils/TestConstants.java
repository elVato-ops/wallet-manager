package walletmanager.utils;

import walletmanager.entity.AccountEntity;
import walletmanager.entity.UserEntity;
import walletmanager.response.AccountResponse;

import java.math.BigDecimal;
import java.util.Currency;

public class TestConstants
{
    public static final Long USER_ID = 17L;
    public static final String USER_NAME = "Bobek";
    public static final String OTHER_USER_NAME = "Chlebek";

    public static final Long ACCOUNT_ID = 997L;
    public static final Currency PLN = Currency.getInstance("PLN");
    public static final BigDecimal BALANCE = BigDecimal.valueOf(100L);

    public static AccountEntity account()
    {
        return new AccountEntity(PLN, BALANCE, user());
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
        return new UserEntity(OTHER_USER_NAME);
    }
}
