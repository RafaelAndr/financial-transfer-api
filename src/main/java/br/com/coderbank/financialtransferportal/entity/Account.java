package br.com.coderbank.financialtransferportal.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Table(name = "CB_ACCOUNT")
@Entity
@Data
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "agency_number", nullable = false)
    private String agencyNumber;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "creation_date", nullable = false, updatable = false)
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    private OffsetDateTime creationDate;

    @Column(name = "client_id")
    private UUID clientId;
}
