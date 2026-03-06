package walletmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import walletmanager.exception.AccountValidationException;
import walletmanager.utils.CurrencyConverter;

import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor
public class AccountEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = CurrencyConverter.class)
    private Currency currency;
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public AccountEntity(Currency currency, BigDecimal balance, UserEntity user)
    {
        if (currency == null)
        {
            throw new AccountValidationException("Currency cannot be null");
        }
        else if (balance.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new AccountValidationException("Balance cannot be negative");
        }
        else if (user == null)
        {
            throw new AccountValidationException("Missing user for the account");
        }

        this.currency = currency;
        this.balance = balance;
        this.user = user;
    }
}