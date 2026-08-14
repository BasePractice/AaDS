package ru.mifi.practice.voln.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.mifi.practice.voln.domain.entity.AuthorityEntity;
import ru.mifi.practice.voln.domain.entity.UserEntity;
import ru.mifi.practice.voln.domain.model.UserModel;
import ru.mifi.practice.voln.domain.model.UserRegistrationRequest;
import ru.mifi.practice.voln.domain.model.UserRepresentation;

/** MapStruct-маппер между сущностью пользователя и её моделями. */
@Mapper
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    UserRepresentation userEntityToUserRepresentation(UserEntity userEntity);

    UserModel userEntityToUserModel(UserEntity entity);

    UserModel userAuthorityToUserModel(UserRegistrationRequest model);

    //TODO: Переносить роли в сущность вместе с их связью на пользователя — AuthorityEntity без неё не создаётся, поэтому здесь они опущены
    @Mapping(target = "authorities", ignore = true)
    UserEntity userModelToUserEntity(UserModel model);

    /** Роль в сущности — объект, в модели — строка; без этого правила список ролей не переносится. */
    default String authorityName(AuthorityEntity authority) {
        return authority.getAuthority();
    }
}
