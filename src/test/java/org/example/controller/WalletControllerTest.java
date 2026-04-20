package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.WalletRequestDTO;
import org.example.exception.NotEnoughMoneyException;
import org.example.exception.WalletNotFoundException;
import org.example.entity.OperationType;
import org.example.entity.Wallet;
import org.example.service.WalletService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    WalletService walletService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void processOperation_walletNotFound_returns404() throws Exception {
        when(walletService.processOperation(any()))
                .thenThrow(new WalletNotFoundException("Wallet is not found"));

        WalletRequestDTO request = new WalletRequestDTO();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Wallet is not found"));
    }

    @Test
    void processOperation_deposit_success() throws Exception {
        UUID walletId = UUID.randomUUID();

        WalletRequestDTO request = new WalletRequestDTO();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(new BigDecimal("100.00"));

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalanceInRub(new BigDecimal("500.00"));

        when(walletService.processOperation(any())).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(500.00))
                .andExpect(jsonPath("$.message").value("Balance changed successfully."));

    }

    @Test
    void processOperation_withdraw_success() throws Exception {
        UUID walletId = UUID.randomUUID();

        WalletRequestDTO request = new WalletRequestDTO();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("100.00"));

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalanceInRub(new BigDecimal("400.00"));

        when(walletService.processOperation(any())).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(400.00))
                .andExpect(jsonPath("$.message").value("Balance changed successfully."));

    }


    @Test
    void processOperation_notEnoughMoney() throws Exception {
        UUID walletId = UUID.randomUUID();

        WalletRequestDTO request = new WalletRequestDTO();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("500.00"));

        when(walletService.processOperation(any()))
                .thenThrow(new NotEnoughMoneyException("Not enough money in wallet"));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Not enough money in wallet"));
    }

    @Test
    void checkAmount_success() throws Exception {
        UUID walletId = UUID.randomUUID();

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalanceInRub(new BigDecimal("400.00"));

        when(walletService.checkBalanceByWalletId(any())).thenReturn(wallet.getBalanceInRub());

        mockMvc.perform(get("/api/v1/wallets/" + walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(400.00));
    }

    @Test
    void checkAmount_walletNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();

        when(walletService.checkBalanceByWalletId(any()))
                .thenThrow(new WalletNotFoundException("Wallet is not found"));

        mockMvc.perform(get("/api/v1/wallets/" + walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Wallet is not found"));
    }

    @Test
    void processOperation_invalid_json() throws Exception {
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
}