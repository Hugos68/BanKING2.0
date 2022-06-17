package com.hugos.BanKING.models;

import com.hugos.BanKING.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity(name = "transaction")
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    // TODO: Fix bank accounts getting registered in database
    @ManyToOne
    @JoinColumn(insertable = false, name = "bankaccount_id")
    private BankAccount from;

    @ManyToOne
    @JoinColumn(insertable = false, name = "bankaccount_id")
    private BankAccount to;

    private Double amount;
}
