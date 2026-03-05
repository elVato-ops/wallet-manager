package account;

import jakarta.persistence.*;
import user.UserEntity;

import java.util.Currency;

@Entity
@Table(name = "accounts")
public class AccountEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    Currency currency;
    Integer balance;

    @ManyToOne
    UserEntity user;
}