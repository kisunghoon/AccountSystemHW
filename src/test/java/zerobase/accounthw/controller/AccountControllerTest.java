package zerobase.accounthw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import zerobase.accounthw.domain.Account;
import zerobase.accounthw.dto.AccountDto;
import zerobase.accounthw.dto.CreateAccount;
import zerobase.accounthw.dto.DeleteAccount;
import zerobase.accounthw.service.AccountService;
import zerobase.accounthw.type.AccountStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successCreateAccount() throws Exception {
        given(accountService.createAccount(anyLong(),anyLong()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());

        mockMvc.perform(
                        post("/account")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new CreateAccount.Request(1L,100L)
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());
    }

    @Test
    void successDeleteAccount() throws Exception {
        given(accountService.deleteAccount(anyLong(),anyString()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());

        mockMvc.perform(
                        delete("/account")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new DeleteAccount.Request(1L,"1234567890")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userid").value(1L))
                .andExpect(jsonPath("$.accountnumber").value("1234567890"))
                .andDo(print());
    }

    @Test
    void successGetAccount() throws Exception {
        given(accountService.getAccounts(anyLong()))
                .willReturn(List.of(
                        AccountDto.builder()
                                .userId(1L)
                                .accountNumber("1234567890")
                                .registeredAt(LocalDateTime.now())
                                .balance(1000L)
                                .build(),
                        AccountDto.builder()
                                .userId(1L)
                                .accountNumber("0987654321")
                                .registeredAt(LocalDateTime.now())
                                .balance(2000L)
                                .build()
                ));

        mockMvc.perform(get("/account/1")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].initialBalance").value(1000L))
                .andExpect(jsonPath("$[1].accountNumber").value("0987654321"))
                .andExpect(jsonPath("$[1].initialBalance").value(2000L))
                .andDo(print());
    }
}