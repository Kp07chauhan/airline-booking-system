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

    public BookingResponseDto bookFlight(String email, BookingRequestDto dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Flight flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        if (flight.getSeatsAvailable() < dto.getNumberOfSeats()) {
            throw new RuntimeException("Not enough seats available");
        }

        double totalPrice = dto.getNumberOfSeats() * flight.getPrice();

        flight.setSeatsAvailable(
                flight.getSeatsAvailable() - dto.getNumberOfSeats()
        );

        flightRepository.save(flight);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setNumberOfSeats(dto.getNumberOfSeats());
        booking.setTotalPrice(totalPrice);
        booking.setBookingTime(LocalDateTime.now());

        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setBookingStatus(BookingStatus.PENDING);

        Booking saved = bookingRepository.save(booking);

        return mapToDto(saved);
    }


    public List<BookingResponseDto> getUserBookings(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByUser(user);

        return bookings.stream()
                .map(this::mapToDto)
                .toList();
    }


    public String cancelBooking(Long bookingId, String email) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));


        if (!booking.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access");
        }


        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            return "Booking already cancelled";
        }


        booking.setBookingStatus(BookingStatus.CANCELLED);

        if (booking.getPaymentStatus() == PaymentStatus.SUCCESS) {
            booking.setPaymentStatus(PaymentStatus.FAILED); // later you can implement REFUND
        }


        Flight flight = booking.getFlight();
        flight.setSeatsAvailable(
                flight.getSeatsAvailable() + booking.getNumberOfSeats()
        );

        flightRepository.save(flight);

        bookingRepository.save(booking);

        return "Booking cancelled successfully";
    }


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