package com.airline.booking.dto.book;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequestDto {
    private Long flightId;
    private Integer numberOfSeats;
}