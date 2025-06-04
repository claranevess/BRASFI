package com.brasfi.platforma.repository;

import com.brasfi.platforma.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByAulaId(Long aulaId);
}

