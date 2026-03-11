package br.com.coderbank.financialtransferportal.dto.request;

import br.com.coderbank.financialtransferportal.enums.TransactionType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequestDto(
        @NotNull(message = "required field")
        TransactionType type,
        @NotNull(message = "required field")
        BigDecimal amount,
        UUID sourceAccount,
        UUID destinationAccount,
        String description
) {
}
