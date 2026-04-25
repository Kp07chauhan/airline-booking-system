package com.airline.booking.entity;

import com.airline.booking.entity.enumEntity.BookingStatus;
import com.airline.booking.entity.enumEntity.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    private Integer numberOfSeats;
    private Double totalPrice;


    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    private LocalDateTime bookingTime;


    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;


    private String paymentId;
}