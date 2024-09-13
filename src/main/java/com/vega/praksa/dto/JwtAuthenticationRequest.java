package com.vega.praksa.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationRequest {

    private String username;

    private String password;

}
