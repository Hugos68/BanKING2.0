package com.hugos.BanKING.bankaccount;


import com.hugos.BanKING.appuser.AppUser;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "bankaccount")
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    public static final String IBAN_PREFIX = "KING BACC";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String IBAN;

    @OneToOne
    @JoinColumn(name = "appuser_id")
    private AppUser appUser;
    private Double balance;
    private LocalDateTime created;
}
