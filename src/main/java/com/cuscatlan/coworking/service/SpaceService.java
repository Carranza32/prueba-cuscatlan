package com.cuscatlan.coworking.service;

import com.cuscatlan.coworking.domain.Space;
import com.cuscatlan.coworking.dto.request.SpaceRequest;
import com.cuscatlan.coworking.dto.response.SpaceResponse;
import com.cuscatlan.coworking.exception.ResourceNotFoundException;
import com.cuscatlan.coworking.mapper.SpaceMapper;
import com.cuscatlan.coworking.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final SpaceMapper spaceMapper;

    @Transactional
    public SpaceResponse create(SpaceRequest request) {
        Space space = spaceMapper.toEntity(request);
        return spaceMapper.toResponse(spaceRepository.save(space));
    }

    public Page<SpaceResponse> findAll(Pageable pageable) {
        return spaceRepository.findByActiveTrue(pageable).map(spaceMapper::toResponse);
    }

    public SpaceResponse findById(Long id) {
        return spaceMapper.toResponse(getOrThrow(id));
    }

    @Transactional
    public SpaceResponse update(Long id, SpaceRequest request) {
        Space space = getOrThrow(id);
        spaceMapper.updateEntity(space, request);
        return spaceMapper.toResponse(space);
    }

    @Transactional
    public void delete(Long id) {
        Space space = getOrThrow(id);
        space.setActive(false);
    }

    private Space getOrThrow(Long id) {
        return spaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espacio no encontrado: " + id));
    }
}
