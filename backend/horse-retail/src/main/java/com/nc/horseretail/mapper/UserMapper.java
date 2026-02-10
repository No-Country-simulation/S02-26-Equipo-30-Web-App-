package com.nc.horseretail.mapper;

import com.nc.horseretail.dto.UserResponse;
import com.nc.horseretail.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);

    User toEntity(UserResponse userResponse);
}
