package com.airline.booking.dto.flight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class FlightResponseDto {
    private Long id;
    private String source;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Double price;
    private Integer seatsAvailable;
}