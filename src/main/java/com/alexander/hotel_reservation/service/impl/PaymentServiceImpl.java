package com.alexander.hotel_reservation.service.impl;

import com.alexander.hotel_reservation.entity.PaystackResponse;
import com.alexander.hotel_reservation.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${paystack.secret-key}")
    private String secretKey;

    @Value("${paystack.base-url}")
    private String baseUrl;

    @Value("${paystack.callback-url}")
    private String callbackUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // initialize payment
    @Override
    public String initializePayment(String email,
                                    double amount,
                                    String reference) {

        int paystackAmount = (int) (amount * 100);

        // request body
        Map<String, Object> body = new HashMap<>();

        body.put("email", email);
        body.put("amount", paystackAmount);

        // send our custom reference to paystack
        body.put("reference", reference);

        // callback after payment
        body.put("callback_url", callbackUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(secretKey);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<PaystackResponse> response = restTemplate.postForEntity(
                baseUrl + "/transaction/initialize",
                request,
                PaystackResponse.class
        );

        if (response.getBody() == null) {
            return null;
        }

        if (!response.getBody().isStatus()) {
            return null;
        }

        // return payment page url
        return response.getBody().getData().getAuthorizationUrl();
    }

    // verify payment
    @Override
    public boolean verifyPayment(String reference) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/transaction/verify/" + reference,
                HttpMethod.GET,
                request,
                Map.class
        );

        if (response.getBody() == null) {
            return false;
        }

        Map data = (Map) response.getBody().get("data");

        if (data == null) {
            return false;
        }

        // check if payment succeeded
        return "success".equals(data.get("status"));
    }
}