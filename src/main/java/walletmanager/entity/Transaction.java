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
public class Transaction
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Convert(converter = CurrencyConverter.class)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Account toAccount;

    public Transaction(BigDecimal amount, Currency currency, Account fromAccount, Account toAccount)
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