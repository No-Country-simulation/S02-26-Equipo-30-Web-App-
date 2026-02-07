package com.nc.horseretail.model.media;

import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "media_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAsset {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Horse horse;

    private String mediaType;
    private LocalDate captureDate;
    private String context;
    private boolean unedited;

    @ManyToOne(fetch = FetchType.LAZY)
    private User uploadedBy;
}