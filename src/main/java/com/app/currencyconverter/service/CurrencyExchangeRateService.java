package com.app.currencyconverter.service;

import com.app.currencyconverter.domain.CurrencyType;
import com.app.currencyconverter.exception.InvalidAmountException;
import com.app.currencyconverter.exception.InvalidCurrencyException;
import com.app.currencyconverter.exception.TimeOutException;
import org.apache.commons.lang3.EnumUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;


/**
 * TODO: Implementation of this class has to be backed by https://api.exchangerate.host/latest?base=EUR&symbols=AUD,CAD,CHF,CNY,GBP,JPY,USD
 */
@Service
public class CurrencyExchangeRateService implements CurrencyService {


    public RestTemplate restTemplate;

    public CacheManager cacheManager;

    @Autowired
    public CurrencyExchangeRateService(RestTemplate restTemplate, CacheManager cacheManager) {
        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String externalServiceUrl = "https://api.exchangerate.host/latest?base=";

    private final String urlTargetCurrency = "&symbols=";


    @Cacheable("conversions")
    //this cache is cleared everyday in the current setup to avoid stale conversion rates. Interval can be changed later if necessary.
    @Override
    public double convert(String source, String target, double amount) {

        if (amount <= 0) throw new InvalidAmountException("The amount should be greater than 0");
        if (null == source || null == target || !EnumUtils.isValidEnum(CurrencyType.class, source) || !EnumUtils.isValidEnum(CurrencyType.class, target)) {
            throw new InvalidCurrencyException("Invalid currencyType. Valid types - [AUD, CAD, CHF, CNY, EUR, GBP, JPY, USD]");
        }
        JSONObject getRate = callExternalService(source, target);
        return (double) getRate.get(target) * amount;
    }


    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    private JSONObject callExternalService(String source, String target) {
        String responseJson = restTemplate.getForObject(externalServiceUrl + source + urlTargetCurrency + target, String.class);
        JSONObject jsonObject = new JSONObject(responseJson);
        return jsonObject.getJSONObject("rates");
    }


    @Recover
    private void backendResponseFallback() {
        //fallback method logic after 3 retries
        logger.error("Cannot connect to external service. Max retries(3) exceeded.");  //Set alert for this log pattern in logging platforms eg:NewRelic to get it as an incident.
        throw new TimeOutException("Timeout waiting for exchangeRate api service");
    }


    @Scheduled(initialDelay = 86400000, fixedRate = 86400000)
    private void evictCacheDaily() {
        Objects.requireNonNull(cacheManager.getCache("conversions")).clear();
    }

}
