package com.nc.horseretail.dto.horse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorseCreateResponse {

    private UUID horseId;
    private UUID listingId;
    private List<UUID> mediaIds;
    private List<UUID> documentIds;
}
