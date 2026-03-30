package com.lms.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Grade name is required")
    private String name;

    @Column(length = 200)
    private String description;

    @OneToMany(mappedBy = "grade", fetch = FetchType.LAZY)
    private List<User> students = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
