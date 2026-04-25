package com.airline.booking.controller;

import com.airline.booking.dto.flight.CreateFlightDto;
import com.airline.booking.dto.flight.FlightResponseDto;
import com.airline.booking.dto.flight.SearchFlightDto;
import com.airline.booking.service.FlightService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;


    @PostMapping("/add")
    public ResponseEntity<FlightResponseDto> addFlight(@RequestBody CreateFlightDto dto) {
        return ResponseEntity.ok(flightService.addFlight(dto));
    }

    @PostMapping("/add-multiple")
    public ResponseEntity<List<FlightResponseDto>> addMultipleFlight(@RequestBody List<CreateFlightDto> dtoList){
        return ResponseEntity.ok(flightService.addMultipleFlight(dtoList));
    }


    @PostMapping("/search")
    public ResponseEntity<List<FlightResponseDto>> searchFlights(@RequestBody SearchFlightDto dto) {
        return ResponseEntity.ok(flightService.searchFlights(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<FlightResponseDto>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }
}