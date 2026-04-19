package com.airline.booking.dto.user;


import lombok.Data;


@Data
public class ResponseUserDto {

   private Long id;
   private String name;
   private String email;

   public ResponseUserDto(Long id, String name, String email) {
      this.id = id;
      this.name = name;
      this.email = email;
   }


}