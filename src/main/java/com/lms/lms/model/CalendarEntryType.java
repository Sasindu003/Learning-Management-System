package com.lms.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "calendar_entry_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEntryType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "Type name is required")
    private String name;

    @Column(length = 20)
    @Builder.Default
    private String color = "#6366f1";

    @Column(length = 10)
    @Builder.Default
    private String icon = "📅";

    @Override
    public String toString() {
        return icon + " " + name;
    }
}
