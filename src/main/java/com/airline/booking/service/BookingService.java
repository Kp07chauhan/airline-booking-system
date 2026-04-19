package com.airline.booking.service;

import com.airline.booking.dto.book.BookingRequestDto;
import com.airline.booking.dto.book.BookingResponseDto;
import com.airline.booking.entity.Booking;
import com.airline.booking.entity.Flight;
import com.airline.booking.entity.User;
import com.airline.booking.entity.enumEntity.BookingStatus;
import com.airline.booking.entity.enumEntity.PaymentStatus;
import com.airline.booking.repository.BookingRepository;
import com.airline.booking.repository.FlightRepository;
import com.airline.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    // ✅ 1. BOOK FLIGHT
    public BookingResponseDto bookFlight(String email, BookingRequestDto dto) {

        // 1️⃣ Get User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Get Flight
        Flight flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        // 3️⃣ Check seats
        if (flight.getSeatsAvailable() < dto.getNumberOfSeats()) {
            throw new RuntimeException("Not enough seats available");
        }

        // 4️⃣ Calculate total price
        double totalPrice = dto.getNumberOfSeats() * flight.getPrice();

        // 5️⃣ Reduce seats
        flight.setSeatsAvailable(
                flight.getSeatsAvailable() - dto.getNumberOfSeats()
        );

        // ✅ IMPORTANT: Save updated flight
        flightRepository.save(flight);

        // 6️⃣ Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setNumberOfSeats(dto.getNumberOfSeats());
        booking.setTotalPrice(totalPrice);
        booking.setBookingTime(LocalDateTime.now());

        // Payment & Booking initial state
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setBookingStatus(BookingStatus.PENDING);

        // 7️⃣ Save booking
        Booking saved = bookingRepository.save(booking);

        // 8️⃣ Return response
        return mapToDto(saved);
    }

    // ✅ 2. GET USER BOOKINGS
    public List<BookingResponseDto> getUserBookings(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByUser(user);

        return bookings.stream()
                .map(this::mapToDto)
                .toList();
    }

    // ✅ 3. CANCEL BOOKING
    public String cancelBooking(Long bookingId, String email) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 🔐 Security check
        if (!booking.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access");
        }

        // ❌ Already cancelled
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            return "Booking already cancelled";
        }

        // 🔄 Update booking status
        booking.setBookingStatus(BookingStatus.CANCELLED);

        // ⚠️ Payment logic (basic)
        if (booking.getPaymentStatus() == PaymentStatus.SUCCESS) {
            booking.setPaymentStatus(PaymentStatus.FAILED); // later you can implement REFUND
        }

        // ♻️ Return seats to flight
        Flight flight = booking.getFlight();
        flight.setSeatsAvailable(
                flight.getSeatsAvailable() + booking.getNumberOfSeats()
        );

        flightRepository.save(flight); // ✅ save updated seats

        bookingRepository.save(booking);

        return "Booking cancelled successfully";
    }

    // ✅ COMMON DTO MAPPER
    private BookingResponseDto mapToDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getFlight().getSource(),
                booking.getFlight().getDestination(),
                booking.getNumberOfSeats(),
                booking.getTotalPrice(),
                booking.getBookingStatus().name(),
                booking.getPaymentStatus().name()
        );
    }
}