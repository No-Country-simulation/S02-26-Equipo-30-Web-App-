package com.nc.horseretail.model.medical;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateRecordId implements Serializable {

    private UUID certificateId;
    private UUID vetRecordId;
}