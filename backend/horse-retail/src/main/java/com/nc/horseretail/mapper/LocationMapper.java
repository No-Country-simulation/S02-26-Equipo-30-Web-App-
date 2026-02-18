package com.nc.horseretail.mapper;

import com.nc.horseretail.dto.LocationRequest;
import com.nc.horseretail.dto.LocationResponse;
import com.nc.horseretail.model.horse.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location toEntity(LocationRequest request);

    LocationResponse toDto(Location location);
}