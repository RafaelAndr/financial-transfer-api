package br.com.coderbank.financialtransferportal.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountResponseDto(
        UUID id,
        String agencyNumber,
        String accountNumber,
        BigDecimal balance,
        OffsetDateTime creationDate
) {
}
