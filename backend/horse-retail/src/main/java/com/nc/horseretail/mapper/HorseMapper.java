package com.nc.horseretail.mapper;

import com.nc.horseretail.dto.horse.HorseRequest;
import com.nc.horseretail.dto.horse.HorseResponse;
import com.nc.horseretail.model.horse.Horse;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {LocationMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface HorseMapper {

    // ==============================
    // CREATE
    // ==============================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Horse toEntity(HorseRequest request);

    // ==============================
    // READ
    // ==============================
    HorseResponse toDto(Horse horse);

    // ==============================
    // UPDATE
    // ==============================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateEntityFromDto(HorseRequest request, @MappingTarget Horse horse);
}