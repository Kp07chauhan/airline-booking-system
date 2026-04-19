package com.airline.booking.dto.flight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchFlightDto {
    private String source;
    private String destination;
}
