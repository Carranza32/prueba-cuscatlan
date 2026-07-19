package com.cuscatlan.coworking.repository;

import com.cuscatlan.coworking.domain.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    Page<Space> findByActiveTrue(Pageable pageable);
}
