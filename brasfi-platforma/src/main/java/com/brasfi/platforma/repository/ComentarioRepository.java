package com.brasfi.platforma.repository;

import com.brasfi.platforma.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByAulaIdAndParentComentarioIsNullOrderByDataCriacaoAsc(Long aulaId);
    List<Comentario> findByParentComentarioOrderByDataCriacaoAsc(Comentario parentComentario);
}