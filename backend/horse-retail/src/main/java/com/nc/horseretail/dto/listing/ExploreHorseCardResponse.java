package com.nc.horseretail.dto.listing;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@Schema(description = "Public card response used by the Explore screen")
public class ExploreHorseCardResponse {

    @Schema(description = "Listing identifier", example = "e10b3b7d-1b33-41f2-a5d2-14ccc6eaff9f")
    private UUID listingId;

    @Schema(description = "Associated horse identifier", example = "11111111-2222-3333-4444-555555555555")
    private UUID horseId;

    @Schema(description = "Listing owner identifier", example = "45e47a0e-3421-414f-a88b-30187cdd4a83")
    private UUID ownerId;

    @Schema(description = "Public display name of the owner", example = "Heritage Equestrian")
    private String ownerName;

    @Schema(description = "Horse display name", example = "Ruby Rose")
    private String horseName;

    @Schema(description = "Horse breed", example = "American Paint Horse")
    private String breed;

    @Schema(description = "Horse sex", example = "FEMALE")
    private String sex;

    @Schema(description = "Horse age in years calculated from birthDate", example = "4")
    private Integer ageYears;

    @Schema(description = "Horse height in meters", example = "1.65")
    private Double heightM;

    @Schema(description = "Human readable discipline/main use", example = "Show Jumping")
    private String discipline;

    @Schema(description = "Public price in USD", example = "74916")
    private Double price;

    @Schema(description = "Listing status", example = "ACTIVE")
    private String listingStatus;

    @Schema(description = "Trust score from 0 to 100", example = "96")
    private Double trustScore;

    @Schema(description = "Human readable trust label", example = "Excellent")
    private String trustLabel;

    @Schema(description = "Country of the horse location", example = "USA")
    private String country;

    @Schema(description = "Region/state of the horse location", example = "CA")
    private String region;

    @Schema(description = "City of the horse location", example = "Woodside")
    private String city;

    @Schema(description = "Preformatted location label for card UIs", example = "Woodside, CA")
    private String locationLabel;

    @Schema(description = "First public image associated with the horse", example = "https://res.cloudinary.com/demo/image/upload/sample.jpg")
    private String coverImageUrl;

    @Schema(description = "True when the horse has at least one public video", example = "true")
    private Boolean hasVideo;

    @Schema(description = "Reserved flag for premium/VIP badges. It is currently false because backend has no premium model yet.", example = "false")
    private Boolean vip;
}
