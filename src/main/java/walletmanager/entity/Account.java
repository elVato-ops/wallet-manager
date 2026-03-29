package walletmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import walletmanager.exception.AccountValidationException;
import walletmanager.exception.IllegalTransactionException;
import walletmanager.exception.InsufficientFundsException;
import walletmanager.utils.CurrencyConverter;

import java.math.BigDecimal;
import java.util.Currency;

import static java.math.BigDecimal.ZERO;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor
public class Account
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = CurrencyConverter.class)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Version
    Long version;

    public Account(Currency currency, BigDecimal balance, User user)
    {
        if (currency == null)
        {
            throw new AccountValidationException("Currency cannot be null");
        }
        else if (balance.compareTo(ZERO) < 0)
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

    public void withdraw(BigDecimal amount)
    {
        if (amount.compareTo(ZERO) < 0)
        {
            throw new IllegalTransactionException("Cannot withdraw negative amount");
        }
        else if (balance.compareTo(amount) < 0)
        {
            throw new InsufficientFundsException();
        }
        else balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount)
    {
        if (amount.compareTo(ZERO) < 0)
        {
            throw new IllegalTransactionException("Cannot deposit negative amount");
        }
        else balance = balance.add(amount);
    }

    public boolean hasCurrency(Currency currency)
    {
        return this.currency.equals(currency);
    }
}