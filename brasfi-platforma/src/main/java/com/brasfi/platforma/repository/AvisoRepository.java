package com.brasfi.platforma.repository;

import com.brasfi.platforma.model.Aviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvisoRepository extends JpaRepository<Aviso, Long> {
}
//o Jpa já tem os métodos no diagrama de classes como salvar e listar (save), e listar (findAll)