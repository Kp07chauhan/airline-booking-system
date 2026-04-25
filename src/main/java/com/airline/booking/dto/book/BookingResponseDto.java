package com.airline.booking.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponseDto {

    private Long bookingId;
    private String flightSource;
    private String flightDestination;
    private Integer seats;
    private Double totalPrice;
    private String status;
    private String paymentStatus;

    public BookingResponseDto(Long bookingId, String flightSource, String flightDestination, Integer seats, Double totalPrice, String status, String paymentStatus) {
        this.bookingId = bookingId;
        this.flightSource = flightSource;
        this.flightDestination = flightDestination;
        this.seats = seats;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public BookingResponseDto(Long id, Long id1, Integer numberOfSeats, Double totalPrice, String name, String name1) {
    }
}