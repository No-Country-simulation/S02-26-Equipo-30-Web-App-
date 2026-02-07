package com.nc.horseretail.model.medical;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_certificate_records")
public class CertificateRecord {

    @EmbeddedId
    private CertificateRecordId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("certificateId")
    @JoinColumn(name = "certificate_id")
    private MedicalCertificate certificate;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vetRecordId")
    @JoinColumn(name = "vet_record_id")
    private VeterinaryRecord veterinaryRecord;
}