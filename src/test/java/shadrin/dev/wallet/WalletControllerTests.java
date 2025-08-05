package shadrin.dev.wallet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shadrin.dev.wallet.controller.WalletController;
import shadrin.dev.wallet.service.WalletService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * тесты для эндпоинтов
 */
@WebMvcTest(WalletController.class)
class WalletControllerTests {

    @MockitoBean
    private WalletService walletService;

    @Autowired
    MockMvc mockMvc;

    /**
     * валидное пополнение
     */
    @Test
    void depositTest_Return200() throws Exception {
        //тестовые данные
        UUID testId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        BigDecimal amount = new BigDecimal("1000");

        doNothing().when(walletService).deposit(eq(testId), eq(amount));

        //запрос
        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "walletId": "00000000-0000-0000-0000-000000000001",
                                            "operationType": "DEPOSIT",
                                            "amount": 1000
                                        }
                                        """
                        ))
                .andExpect(status().isOk());
        verify(walletService, times(1))
                .deposit(eq(testId), eq(amount));

    }

    /**
     * не валидное пополнение
     */
    @Test
    void depositTest_Return400() throws Exception {
        UUID testId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        doThrow(new IllegalArgumentException("Deposit amount must be positive"))
                .when(walletService).deposit(eq(testId), eq(BigDecimal.ZERO));

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "walletId": "00000000-0000-0000-0000-000000000001",
                                            "operationType": "DEPOSIT",
                                            "amount":0
                                        }
                                        """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Deposit amount must be positive"));
    }

    /**
     * для валидного вывода средств
     */
    @Test
    void withdrawTest_Return200() throws Exception {
        UUID testId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        BigDecimal amount = new BigDecimal("1000");

        doNothing().when(walletService).withdraw(eq(testId), eq(amount));

        //запрос
        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "walletId": "00000000-0000-0000-0000-000000000001",
                                            "operationType": "WITHDRAW",
                                            "amount": 1000
                                        }
                                        """
                        ))
                .andExpect(status().isOk());
        verify(walletService, times(1))
                .withdraw(eq(testId), eq(amount));
    }

    /**
     * не валидный вывод средств
     */
    @Test
    void withdrawTest_Return400() throws Exception {
        UUID testId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        doThrow(new IllegalArgumentException("Can't withdraw 0 or negative amount"))
                .when(walletService).withdraw(eq(testId), eq(BigDecimal.ZERO));

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "walletId": "00000000-0000-0000-0000-000000000001",
                                            "operationType": "WITHDRAW",
                                            "amount":0
                                        }
                                        """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Can't withdraw 0 or negative amount"));
    }

}
