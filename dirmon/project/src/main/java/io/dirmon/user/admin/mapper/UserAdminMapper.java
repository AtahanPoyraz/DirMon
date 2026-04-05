package io.dirmon.user.admin.mapper;

import io.dirmon.user.admin.dto.CreateUserRequest;
import io.dirmon.user.admin.dto.UpdateUserRequest;
import io.dirmon.user.admin.dto.UserDto;
import io.dirmon.user.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserAdminMapper {
    UserDto toDto(UserModel entity);
    UserModel toEntity(CreateUserRequest createUserRequest);

    void updateUserToEntity(UpdateUserRequest updateUserRequest, @MappingTarget UserModel entity);
}
