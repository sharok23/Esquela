package com.sharok.esquela.contract.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StudentRequest {
    @NotBlank(message = "First name should not be blank")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name should not be blank")
    private String lastName;

    @NotBlank(message = "date of birth should not be blank")
    private String dob;

    @NotBlank(message = "Gender should not be blank")
    private String gender;

    @NotBlank(message = "Address should not be blank")
    private String address;

    @NotNull(message = "Phone number is required")
    private Integer phoneNumber;
}
