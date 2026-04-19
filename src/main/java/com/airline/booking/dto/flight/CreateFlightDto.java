package com.airline.booking.dto.flight;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateFlightDto {
    private String source;
    private String destination;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private Double price;
    private Integer seatsAvailable;
}
