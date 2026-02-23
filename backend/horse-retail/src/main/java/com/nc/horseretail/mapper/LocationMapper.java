package com.nc.horseretail.mapper;

import com.nc.horseretail.dto.horse.LocationRequest;
import com.nc.horseretail.dto.horse.LocationResponse;
import com.nc.horseretail.model.horse.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location toEntity(LocationRequest request);

    LocationResponse toDto(Location location);
}