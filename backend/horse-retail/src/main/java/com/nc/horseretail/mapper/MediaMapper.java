package com.nc.horseretail.mapper;

import com.nc.horseretail.dto.MediaResponse;
import com.nc.horseretail.model.media.MediaAsset;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MediaMapper {

    MediaResponse toResponse(MediaAsset media);

    List<MediaResponse> toResponseList(List<MediaAsset> mediaList);
}