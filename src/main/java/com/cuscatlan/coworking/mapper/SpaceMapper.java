package com.cuscatlan.coworking.mapper;

import com.cuscatlan.coworking.domain.Space;
import com.cuscatlan.coworking.dto.request.SpaceRequest;
import com.cuscatlan.coworking.dto.response.SpaceResponse;
import org.springframework.stereotype.Component;

@Component
public class SpaceMapper {
    public Space toEntity(SpaceRequest request) {
        return Space.builder()
                .name(request.name())
                .type(request.type())
                .capacity(request.capacity())
                .location(request.location())
                .hourlyRate(request.hourlyRate())
                .active(true)
                .build();
    }

    public void updateEntity(Space space, SpaceRequest request) {
        space.setName(request.name());
        space.setType(request.type());
        space.setCapacity(request.capacity());
        space.setLocation(request.location());
        space.setHourlyRate(request.hourlyRate());
    }

    public SpaceResponse toResponse(Space space) {
        return new SpaceResponse(
                space.getId(), space.getName(), space.getType(), space.getCapacity(),
                space.getLocation(), space.getHourlyRate(), space.isActive(),
                space.getCreatedAt(), space.getUpdatedAt()
        );
    }
}
