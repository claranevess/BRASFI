package com.brasfi.platforma.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data

public class Aviso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id é gerado automaticamente
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String conteudo;
    private LocalDate data;

    @ManyToOne //vários avisos podem ter o mesmo autor
    @JoinColumn(name = "administrador_id") //nome personalizado no banco de dados
    private Administrador administrador; //só quem cria é adm

    private LocalTime horario;   // localtime já que ta como dropdown no front
    private String duracao;   // no front tá como string
    private String link;
}