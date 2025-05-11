package com.brasfi.platforma.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

/*@Embeddable: indica que essa classe
não é uma entidade própria, mas será
inserida como parte de outra entidade (User).
A gente usa pq Endereco é um atributo composto de User.*/

@Data
@Embeddable
public class Endereco {
    private String cidade;
    private String estado;
    private String pais;
}
