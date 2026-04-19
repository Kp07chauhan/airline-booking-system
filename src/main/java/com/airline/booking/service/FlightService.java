package com.airline.booking.service;

import com.airline.booking.dto.flight.CreateFlightDto;
import com.airline.booking.dto.flight.FlightResponseDto;
import com.airline.booking.dto.flight.SearchFlightDto;
import com.airline.booking.entity.Flight;
import com.airline.booking.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    // ✅ ADMIN - Add Flight
    public FlightResponseDto addFlight(CreateFlightDto dto) {

        Flight flight = new Flight();
        flight.setSource(dto.getSource());
        flight.setDestination(dto.getDestination());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setPrice(dto.getPrice());
        flight.setSeatsAvailable(dto.getSeatsAvailable());

        Flight saved = flightRepository.save(flight);

        return mapToDto(saved);
    }

    // ✅ USER - Search Flights
    public List<FlightResponseDto> searchFlights(SearchFlightDto dto) {

        return flightRepository
                .findBySourceAndDestination(dto.getSource(), dto.getDestination())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    // ✅ Common Mapper
    private FlightResponseDto mapToDto(Flight flight) {
        return new FlightResponseDto(
                flight.getId(),
                flight.getSource(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.getSeatsAvailable()
        );
    }
}
