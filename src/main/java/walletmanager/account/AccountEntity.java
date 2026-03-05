package walletmanager.account;

import jakarta.persistence.*;
import walletmanager.user.UserEntity;

import java.util.Currency;

@Entity
@Table(name = "accounts")
public class AccountEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Currency currency;
    private Integer balance;

    @ManyToOne
    private UserEntity user;
}