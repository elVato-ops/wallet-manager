package walletmanager.service;

import walletmanager.account.AccountEntity;
import walletmanager.request.CreateAccountRequest;
import walletmanager.response.AccountResponse;
import walletmanager.user.UserEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountMapper
{
    public static AccountEntity toEntity(CreateAccountRequest request, UserEntity user)
    {
        return new AccountEntity(request.currency(), request.balance(), user);
    }

    public static AccountResponse toResponse(AccountEntity entity)
    {
        return new AccountResponse(entity.getId(), entity.getCurrency(), entity.getBalance(), entity.getUser().getId());
    }

    public static Set<AccountResponse> toResponse(List<AccountEntity> accounts)
    {
        return accounts.stream()
                .map(AccountMapper::toResponse)
                .collect(Collectors.toSet());
    }
}
