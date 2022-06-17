package com.hugos.BanKING.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

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
    @JoinColumn(name = "appuser_id")
    private AppUser appUser;
    private Double balance;
}
