package walletmanager.utils;

import walletmanager.entity.Account;
import walletmanager.entity.User;
import walletmanager.request.CreateAccountRequest;
import walletmanager.response.AccountResponse;

import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper
{
    public static Account toEntity(CreateAccountRequest request, User user)
    {
        return new Account(request.currency(), request.balance(), user);
    }

    public static AccountResponse toResponse(Account entity)
    {
        return new AccountResponse(entity.getId(), entity.getCurrency(), entity.getBalance(), entity.getUser().getId());
    }

    public static List<AccountResponse> toResponse(List<Account> accounts)
    {
        return accounts.stream()
                .map(AccountMapper::toResponse)
                .collect(Collectors.toList());
    }
}
