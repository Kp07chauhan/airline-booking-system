package com.airline.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private String destination;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private Double price;

    private Integer seatsAvailable;
}