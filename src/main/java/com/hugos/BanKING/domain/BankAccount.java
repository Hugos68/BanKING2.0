package com.hugos.BanKING.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    public static final String IBAN_PREFIX = "KING BACC";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String IBAN;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private Double balance;
    private LocalDateTime created;
}
