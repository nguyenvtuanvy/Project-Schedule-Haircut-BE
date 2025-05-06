package com.example.projectschedulehaircutserver.controller.web;

import com.example.projectschedulehaircutserver.service.vnpay.VnPayService;
import com.example.projectschedulehaircutserver.service.zalopay.ZaloPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/web/payment")
@RequiredArgsConstructor
public class PaymentReturnController {
    private final VnPayService vnPayService;
    private final ZaloPayService zaloPayService;

    @GetMapping("/vnpay/return")
    public void handlePaymentVNPayReturn(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            vnPayService.processPaymentReturn(request, response);
        } catch (IOException e) {
            throw new RuntimeException("Payment return processing failed", e);
        }
    }

    @GetMapping("/zalopay/return")
    public void handlePaymentZaloPayReturn(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            zaloPayService.processPaymentReturn(request, response);
        } catch (IOException e) {
            throw new RuntimeException("Payment return processing failed", e);
        }
    }
}
