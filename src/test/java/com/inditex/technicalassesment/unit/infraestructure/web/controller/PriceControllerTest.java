package com.inditex.technicalassesment.unit.infraestructure.web.controller;

import com.inditex.technicalassesment.application.dto.response.PriceResponse;
import com.inditex.technicalassesment.application.port.in.FindApplicablePriceUseCase;
import com.inditex.technicalassesment.domain.exception.PriceNotFoundException;
import com.inditex.technicalassesment.infrastructure.in.web.controller.PriceController;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindApplicablePriceUseCase findApplicablePriceUseCase;

    @Test
    void shouldReturnPriceWhenValidRequest() throws Exception {

        PriceResponse response = PriceResponse.builder()
                .productId(35455L)
                .brandId(1L)
                .priceList(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .price(new BigDecimal("35.50"))
                .build();

        when(findApplicablePriceUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/prices/applicable")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50));

        verify(findApplicablePriceUseCase).execute(any());
    }

    @Test
    void shouldReturn404WhenPriceNotFound() throws Exception {

        when(findApplicablePriceUseCase.execute(any()))
                .thenThrow(new PriceNotFoundException("No price found"));

        mockMvc.perform(get("/api/v1/prices/applicable")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No price found"));
    }

    @Test
    void shouldReturn400WhenMissingRequiredParameter() throws Exception {

        mockMvc.perform(get("/api/v1/prices/applicable")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenInvalidDateFormat() throws Exception {

        mockMvc.perform(get("/api/v1/prices/applicable")
                        .param("applicationDate", "invalid-date")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());
    }
}
