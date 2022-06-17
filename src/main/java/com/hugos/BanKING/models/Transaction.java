package com.hugos.BanKING.models;

import com.hugos.BanKING.enums.TransactionType;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "transaction")
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne
    private BankAccount fromBankAccount;

    @ManyToOne
    private BankAccount toBankAccount;
    private Double amount;
    private LocalDateTime dateTime;

}
