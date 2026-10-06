package io.hexlet.spring.mapper;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class PostMapper {

    @Mapping(target = "userId", source = "user.id")
    public abstract PostDTO toDTO(Post post);

    /**
     * ВНИМАНИЕ: у сущностей Post и User есть одноимённые свойства
     * (id, createdAt, updatedAt). Когда у метода несколько source-параметров,
     * MapStruct сопоставляет целевые свойства по именам свойств ВСЕХ параметров
     * сразу, поэтому без явных правил он подставил бы user.getId() в post.id,
     * user.getCreatedAt() в post.createdAt, user.getUpdatedAt() в post.updatedAt,
     * а свойство post.user вообще осталось бы пустым.
     */
    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "published", constant = "true")
    public abstract Post toEntity(PostCreateDTO postCreateDTO, User user);

    public abstract void updateEntity(@MappingTarget Post post, PostUpdateDTO postUpdateDTO);
}
