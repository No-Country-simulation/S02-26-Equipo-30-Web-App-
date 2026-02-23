package com.nc.horseretail.mapper;

import com.nc.horseretail.dto.ListingRequest;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.model.listing.Listing;
import org.mapstruct.*;


@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ListingMapper {

    // =========================
    // CREATE
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "horse", ignore = true)
    @Mapping(target = "askingPriceUsd", source = "price")
    Listing toEntity(ListingRequest request);

    // =========================
    // READ
    // =========================
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.fullName")
    @Mapping(target = "price", source = "askingPriceUsd")
    @Mapping(target = "status", source = "status")
    ListingResponse toDto(Listing listing);

    // =========================
    // UPDATE
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "horse", ignore = true)
    @Mapping(target = "askingPriceUsd", source = "price")
    void updateFromRequest(ListingRequest request, @MappingTarget Listing listing);
}