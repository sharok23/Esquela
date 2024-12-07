package com.sharok.esquela.contract.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StudentResponse {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String gender;
    private String address;
    private int phoneNumber;
}
