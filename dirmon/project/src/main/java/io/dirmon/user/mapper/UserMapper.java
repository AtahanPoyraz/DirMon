package io.dirmon.user.mapper;

import io.dirmon.user.dto.UpdateDetailsRequest;
import io.dirmon.user.dto.UserDto;
import io.dirmon.user.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDto toDto(UserModel entity);
    void updateUserDetailsToEntity(UpdateDetailsRequest updateDetailsRequest, @MappingTarget UserModel entity);
}
