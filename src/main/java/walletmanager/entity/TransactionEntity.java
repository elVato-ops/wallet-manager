package walletmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import walletmanager.exception.TransactionValidationException;
import walletmanager.utils.CurrencyConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@Getter
public class TransactionEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Convert(converter = CurrencyConverter.class)
    @Column(nullable = false)
    private Currency currency;

    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity toAccount;

    public TransactionEntity(BigDecimal amount, Currency currency, AccountEntity fromAccount, AccountEntity toAccount)
    {
        if (currency == null)
        {
            throw new TransactionValidationException("Transaction currency cannot be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new TransactionValidationException("Transaction amount must be positive");
        }

        if (fromAccount == null || toAccount == null)
        {
            throw new TransactionValidationException("Transaction accounts cannot be null");
        }

        this.amount = amount;
        this.currency = currency;
        this.timestamp = LocalDateTime.now();
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }
}