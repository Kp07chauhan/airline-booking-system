package com.airline.booking.service;

import com.airline.booking.entity.Booking;
import com.airline.booking.entity.Flight;
import com.airline.booking.entity.enumEntity.BookingStatus;
import com.airline.booking.entity.enumEntity.PaymentStatus;
import com.airline.booking.repository.BookingRepository;
import com.airline.booking.repository.FlightRepository;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // =========================================================
    // ✅ 1. CREATE PAYMENT LINK
    // =========================================================
    public String createPaymentLink(Long bookingId) throws Exception {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // ❌ prevent duplicate payment
        if (booking.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new RuntimeException("Payment already completed");
        }

        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();

        // ✅ FIX: amount must be integer (paisa)
        options.put("amount", (int)(booking.getTotalPrice() * 100));
        options.put("currency", "INR");
        options.put("description", "Flight Booking Payment");

        JSONObject customer = new JSONObject();
        customer.put("name", booking.getUser().getName());
        customer.put("email", booking.getUser().getEmail());
        options.put("customer", customer);

        // ✅ CALLBACK URL (VERY IMPORTANT)
        options.put(
                "callback_url",
                "http://localhost:8080/api/airline/booking/payment-success?bookingId=" + bookingId
        );

        options.put("callback_method", "get");

        // ✅ Create payment link
        com.razorpay.PaymentLink link = client.paymentLink.create(options);

        return link.get("short_url");
    }

    // =========================================================
    // ✅ 2. VERIFY SIGNATURE (SECURITY)
    // =========================================================
    public boolean verifySignature(String orderId, String paymentId, String razorpaySignature) {

        try {
            String data = orderId + "|" + paymentId;

            SecretKeySpec secretKey = new SecretKeySpec(keySecret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);

            byte[] rawHmac = mac.doFinal(data.getBytes());
            String generatedSignature = Base64.getEncoder().encodeToString(rawHmac);

            return generatedSignature.equals(razorpaySignature);

        } catch (Exception e) {
            throw new RuntimeException("Signature verification failed");
        }
    }

    // =========================================================
    // ✅ 3. UPDATE PAYMENT STATUS
    // =========================================================
    public Booking updatePaymentStatus(Long bookingId, boolean success, String paymentId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (success) {
            booking.setPaymentStatus(PaymentStatus.SUCCESS);
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            booking.setPaymentId(paymentId);

        } else {
            booking.setPaymentStatus(PaymentStatus.FAILED);
            booking.setBookingStatus(BookingStatus.CANCELLED);

            // ♻️ return seats
            Flight flight = booking.getFlight();
            flight.setSeatsAvailable(
                    flight.getSeatsAvailable() + booking.getNumberOfSeats()
            );

            flightRepository.save(flight); // ✅ IMPORTANT
        }

        return bookingRepository.save(booking);
    }
}