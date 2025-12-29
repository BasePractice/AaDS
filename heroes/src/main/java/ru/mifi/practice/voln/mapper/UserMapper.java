package ru.mifi.practice.voln.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.mifi.practice.voln.domain.entity.UserEntity;
import ru.mifi.practice.voln.domain.model.UserModel;
import ru.mifi.practice.voln.domain.model.UserRegistrationRequest;
import ru.mifi.practice.voln.domain.model.UserRepresentation;

@Mapper
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    UserRepresentation userEntityToUserRepresentation(UserEntity userEntity);

    UserModel userEntityToUserModel(UserEntity entity);

    UserModel userAuthorityToUserModel(UserRegistrationRequest model);

    UserEntity userModelToUserEntity(UserModel model);

}
