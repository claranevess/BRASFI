package com.brasfi.platforma.dto;

import jakarta.validation.Valid; // Keep this if you use @Valid on nested DTOs (e.g., in Trilha or other contexts)
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// import lombok.Data; // REMOVE THIS LINE
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class AulaFormDTO { // Change from @Data to plain class

    private Long id;
    @NotBlank(message = "O título da aula não pode estar em branco.")
    @Size(max = 255, message = "O título da aula não pode ter mais de 255 caracteres.")
    private String titulo;
    @NotBlank(message = "O link da aula (vídeo) não pode estar em branco.")
    private String link;
    private String descricao;
    private List<MaterialFormDTO> materiais = new ArrayList<>();

    // Manually add Getters and Setters:
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<MaterialFormDTO> getMateriais() { return materiais; }
    public void setMateriais(List<MaterialFormDTO> materiais) { this.materiais = materiais; }

    public boolean isEmpty() {
        return (titulo == null || titulo.trim().isEmpty()) &&
                (link == null || link.trim().isEmpty());
    }
}