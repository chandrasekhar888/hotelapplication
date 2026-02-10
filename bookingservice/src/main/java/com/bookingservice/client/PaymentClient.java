package com.bookingservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookingservice.dto.ProductRequest;
import com.bookingservice.dto.StripeResponse;

@FeignClient(name = "PAYMENTSERVICE") // Must match your service name in Eureka or application.yml
public interface PaymentClient {

    @PostMapping("/product/v1/checkout")
    public StripeResponse checkoutProducts(@RequestBody ProductRequest productRequest);

    @GetMapping("/product/v1/success")
    public String handleSuccess(@RequestParam("session_id") String sessionId);

    @GetMapping("/product/v1/cancel")
    public String handleCancel();
}
