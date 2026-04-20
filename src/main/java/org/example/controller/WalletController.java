package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.BalanceResponseDTO;
import org.example.dto.WalletRequestDTO;
import org.example.dto.WalletResponseDTO;
import org.example.entity.Wallet;
import org.example.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping(value = "/wallet")
    public ResponseEntity<WalletResponseDTO> processOperation(@Valid @RequestBody WalletRequestDTO walletRequestDTO) {
        Wallet wallet = walletService.processOperation(walletRequestDTO);
        WalletResponseDTO wrd = new WalletResponseDTO(
                wallet.getId(),
                wallet.getBalanceInRub(),
                "Balance changed successfully.");
        return new ResponseEntity<>(wrd, HttpStatus.OK);
    }

    @GetMapping(value = "/wallets/{id}")
    public ResponseEntity<BalanceResponseDTO> checkAmount(@PathVariable UUID id) {
        BigDecimal balance = walletService.checkBalanceByWalletId(id);
        BalanceResponseDTO brd = new BalanceResponseDTO(id,balance);
        return new ResponseEntity<>(brd, HttpStatus.OK);
    }
}
