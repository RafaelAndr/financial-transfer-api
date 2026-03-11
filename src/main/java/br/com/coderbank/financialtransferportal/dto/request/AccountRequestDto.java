package br.com.coderbank.financialtransferportal.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AccountRequestDto(
        @NotNull(message = "Required field")
        UUID clientId
) {
}
