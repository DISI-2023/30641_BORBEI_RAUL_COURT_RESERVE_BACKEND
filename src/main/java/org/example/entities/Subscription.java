package org.example.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Subscription")
public class Subscription {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID id;

    @Column(name = "day", nullable = false)
    private DayOfWeek dayOfWeek;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @Column(name = "startTime", nullable = false)
    private LocalDate startTime;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @Column(name = "endTime", nullable = false)
    private LocalDate endTime;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="hh:mm:ss")
    @Column(name = "startHour", nullable = false)
    private LocalTime startHour;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="hh:mm:ss")
    @Column(name = "endHour", nullable = false)
    private LocalTime endHour;

    @Column(name = "finalPrice", nullable = false)
    private double finalPrice;

    @Column(name = "type", nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "appUser_id")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;

}
