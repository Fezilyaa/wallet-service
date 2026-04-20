package org.example.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.entity.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletRequestDTO {

    @NotNull(message = "walletId must not be null")
    private UUID walletId;

    @NotNull(message = "operationType must not be null")
    private OperationType operationType;

    @NotNull(message = "amount must not be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "amount must be bigger than 0")
    private BigDecimal amount;
}
