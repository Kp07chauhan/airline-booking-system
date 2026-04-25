package com.airline.booking.controller;

import com.airline.booking.dto.book.*;
import com.airline.booking.dto.book.BookingRequestDto;
import com.airline.booking.dto.book.BookingResponseDto;
import com.airline.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {


    private final BookingService bookingService;




    @PostMapping
    public BookingResponseDto bookFlight(@RequestBody BookingRequestDto dto) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return bookingService.bookFlight(email, dto);
    }


    @GetMapping("/my")
    public List<BookingResponseDto> myBookings() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return bookingService.getUserBookings(email);
    }

    @DeleteMapping("/{id}")
    public String cancel(@PathVariable Long id, Authentication auth) {
        return bookingService.cancelBooking(id, auth.getName());
    }
}