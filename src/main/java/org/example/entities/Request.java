package org.example.entities;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Request")
public class Request {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID id;

    @Column(name = "take_over", nullable = false)
    private boolean take_over;

    @ManyToOne
    @JoinColumn(name = "posted_by", nullable = false)
    private AppUser postedBy;

    @ManyToOne
    @JoinColumn(name = "taken_by")
    private AppUser takenBy;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

}
