package walletmanager.utils;

import org.springframework.stereotype.Component;
import walletmanager.entity.Account;
import walletmanager.entity.User;
import walletmanager.request.CreateAccountRequest;
import walletmanager.response.AccountResponse;

@Component
public class AccountMapper
{
    public Account toEntity(CreateAccountRequest request, User user)
    {
        return new Account(request.currency(), request.balance(), user);
    }

    public AccountResponse toResponse(Account entity)
    {
        return new AccountResponse(entity.getId(), entity.getCurrency(), entity.getBalance(), entity.getUser().getId());
    }
}
