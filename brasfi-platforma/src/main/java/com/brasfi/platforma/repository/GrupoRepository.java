package com.brasfi.platforma.repository;

import com.brasfi.platforma.model.Grupo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    List<Grupo> findByMembros_Id(Long userId);
}
