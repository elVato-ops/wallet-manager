package walletmanager.transaction;

import walletmanager.account.AccountEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Currency;

@Entity
@Table(name = "transactions")
public class TransactionEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer amount;
    private Currency currency;
    private LocalDateTime timestamp;

    @ManyToOne
    private AccountEntity fromAccount;

    @ManyToOne
    private AccountEntity toAccount;
}
