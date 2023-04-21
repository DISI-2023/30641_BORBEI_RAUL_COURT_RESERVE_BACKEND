package org.example.entities;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Reservation")
public class Reservation {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID id;

    @Column(name = "startTime", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "endTime", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "finalPrice", nullable = false)
    private double finalPrice;

    @ManyToOne
    @JoinColumn(name = "appUser_id")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;

    @OneToOne(mappedBy = "reservation")
    private Request request;

    @Column(name = "tariffType", nullable = false)
    private String tariffType;
}
