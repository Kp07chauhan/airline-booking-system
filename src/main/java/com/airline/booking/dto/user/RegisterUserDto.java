package com.airline.booking.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterUserDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
