package br.com.coderbank.financialtransferportal.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AccountResponseDto(
        UUID accountId,
        String agencyNumber,
        String accountNumber,
        BigDecimal balance,
        LocalDate creationDate
) {
}
