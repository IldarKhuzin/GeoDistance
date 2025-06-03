package ru.ildar.geodistance.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String address;

    @Column(name = "yandex_latitude", nullable = false)
    private Double yandexLatitude;

    @Column(name = "yandex_longitude", nullable = false)
    private Double yandexLongitude;

    @Column(name = "dadata_latitude", nullable = false)
    private Double dadataLatitude;

    @Column(name = "dadata_longitude", nullable = false)
    private Double dadataLongitude;

    @Column(name = "distance_meters")
    private Double distanceMeters;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
