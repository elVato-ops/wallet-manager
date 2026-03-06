package walletmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    private BigDecimal amount;

    @Convert(converter = CurrencyConverter.class)
    private Currency currency;
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity toAccount;

//    this.timestamp = LocalDateTime.now();
}
