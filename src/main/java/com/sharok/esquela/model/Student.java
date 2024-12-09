package com.sharok.esquela.model;

import com.sharok.esquela.constant.Gender;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;
    private int phoneNumber;
    @Setter private LocalDateTime uploadedAt;
}
