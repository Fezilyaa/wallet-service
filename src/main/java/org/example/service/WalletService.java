package org.example.service;

import org.example.dto.WalletRequestDTO;
import org.example.entity.OperationType;
import org.example.exception.NotEnoughMoneyException;
import org.example.exception.WalletNotFoundException;
import org.example.entity.Wallet;
import org.example.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional()
    public Wallet processOperation (WalletRequestDTO walletRequestDTO) {
        Wallet wallet = walletRepository.findByIdWithLock(walletRequestDTO.getWalletId())
                .orElseThrow(()-> new WalletNotFoundException("Wallet is not found"));
        BigDecimal currentBalance = wallet.getBalanceInRub();
        BigDecimal amount = walletRequestDTO.getAmount();

        if(walletRequestDTO.getOperationType().equals(OperationType.DEPOSIT)) {
            wallet.setBalanceInRub(currentBalance.add(amount));
            return wallet;
        }

        if(currentBalance.compareTo(amount) < 0) {
            throw new NotEnoughMoneyException("Not enough money in wallet");
        }

        wallet.setBalanceInRub(currentBalance.subtract(amount));
        return wallet;
    }

    @Transactional(readOnly = true)
    public BigDecimal checkBalanceByWalletId(UUID id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet is not found"));
        return  wallet.getBalanceInRub();
    }
}
