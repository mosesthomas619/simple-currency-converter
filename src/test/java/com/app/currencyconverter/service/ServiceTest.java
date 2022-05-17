package com.app.currencyconverter.service;

import com.app.currencyconverter.exception.InvalidAmountException;
import com.app.currencyconverter.exception.InvalidCurrencyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.cache.CacheManager;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private CurrencyExchangeRateService currencyExchangeRateService;


    @Test
    public void shouldThrowInvalidCurrencyExceptionForInput() {

        Exception exception = Assertions.assertThrows(InvalidCurrencyException.class,
                () -> currencyExchangeRateService.convert("invalid", "EUR", 4));

        String expectedMessage = "Invalid currencyType";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    public void shouldThrowInvalidAmountExceptionForInput() {

        Exception exception = Assertions.assertThrows(InvalidAmountException.class,
                () -> currencyExchangeRateService.convert("USD", "EUR", -4));

        String expectedMessage = "The amount should be greater than 0";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    public void shouldReturnConversionRateForValidInput() {

        String restTemplateMockResponse = "{\"motd\":{\"msg\":\"If you or your company use this project or like what we doing, please consider backing us so we can continue maintaining and evolving this project.\",\"url\":\"https://exchangerate.host/#/donate\"},\"success\":true,\"base\":\"EUR\",\"date\":\"2022-04-30\",\"rates\":{\"USD\":1.054221}}";
        when(restTemplate.getForObject(Mockito.anyString(), any())).thenReturn(restTemplateMockResponse);
        assertEquals(2.108442, currencyExchangeRateService.convert("EUR", "USD", 2));
    }


}
