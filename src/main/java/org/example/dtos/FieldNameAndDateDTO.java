package org.example.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldNameAndDateDTO {

    /**
     * This class is used exclusively by requests coming from FE regarding a field and a date about which
     * information related to reservations availability is required to be extracted from the DB
     * This is why this class has only 2 attributes: the field name and the date
     */
    private String fieldName;
    private LocalDate date;
}
