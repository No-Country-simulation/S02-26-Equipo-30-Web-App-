package com.nc.horseretail.mapper;

import com.nc.horseretail.dto.horse.HorseResponse;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.dto.UserResponse;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ListingMapper.class, HorseMapper.class}
)
public interface UserMapper {

    // ============================
    // PRIVATE DTO
    // ============================

    UserResponse toDto(User user);

    // ============================
    // PUBLIC DTO
    // ============================

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "role", ignore = true)
    UserResponse toPublicDto(User user);

    // ============================
    // NESTED MAPPERS
    // ============================

    ListingResponse toListingDto(Listing listing);

    HorseResponse toHorseDto(Horse horse);
}