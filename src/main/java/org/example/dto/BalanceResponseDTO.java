package org.example.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceResponseDTO(
        UUID walletId,
        BigDecimal balance
) {
}