package com.lms.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "Subject name is required")
    private String name;

    @Column(unique = true, nullable = false, length = 20)
    @NotBlank(message = "Subject code is required")
    private String code;

    @Column(length = 300)
    private String description;

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
