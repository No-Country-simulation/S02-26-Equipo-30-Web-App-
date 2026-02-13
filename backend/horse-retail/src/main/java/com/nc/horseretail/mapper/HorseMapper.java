package com.nc.horseretail.mapper;

import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.model.horse.Horse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HorseMapper {

    Horse toEntity(HorseRequest horse);

    HorseResponse toDto(Horse horse);
}
