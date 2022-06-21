package com.hugos.BanKING.entities;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity(name = "bankaccount")
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    public static final String IBAN_PREFIX = "KING BACC";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String iban;
    @OneToOne
    private AppUser appUser;
    private Double balance;
}