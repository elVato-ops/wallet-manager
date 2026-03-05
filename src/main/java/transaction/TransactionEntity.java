package transaction;

import account.AccountEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Currency;

@Entity
@Table(name = "transactions")
public class TransactionEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    Integer amount;
    Currency currency;
    LocalDateTime timestamp;

    @ManyToOne
    AccountEntity fromAccount;

    @ManyToOne
    AccountEntity toAccount;
}
