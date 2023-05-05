package org.example.entities;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Setter
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Field")
public class Field {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    // I decided to increase the length of the URL from the default value of 255
    // since URLs can get pretty long
    @Column(name = "image_url", length = 1023)
    private String imageUrl;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private List<Reservation> reservationList;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private List<Tariff> tariffList;


}
