package com.vega.praksa.dto;

import lombok.Data;

@Data
public class UserRequest {

    private Long id;

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String email;

}
