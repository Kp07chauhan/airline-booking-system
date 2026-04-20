package com.airline.booking.controller;

import com.airline.booking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;


    @GetMapping("/link/{bookingId}")
    public String generatePaymentLink(@PathVariable Long bookingId) throws Exception {
        return paymentService.createPaymentLink(bookingId);
    }


    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam Long bookingId,
            @RequestParam(required = false) String razorpay_payment_id,
            @RequestParam(required = false) String razorpay_order_id,
            @RequestParam(required = false) String razorpay_signature
    ) {

        boolean isValid = false;

        if (razorpay_payment_id != null) {

            if (razorpay_order_id != null && razorpay_signature != null) {
                isValid = paymentService.verifySignature(
                        razorpay_order_id,
                        razorpay_payment_id,
                        razorpay_signature
                );
            } else {
                // Payment link case (no signature)
                isValid = true;
            }
        }

        if (isValid) {
            paymentService.updatePaymentStatus(
                    bookingId,
                    true,
                    razorpay_payment_id
            );
            return "Payment Successful & Booking Confirmed";
        } else {
            paymentService.updatePaymentStatus(
                    bookingId,
                    false,
                    razorpay_payment_id
            );
            return "Payment Failed";
        }
    }
}