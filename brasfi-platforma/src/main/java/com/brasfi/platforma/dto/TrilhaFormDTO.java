package com.brasfi.platforma.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import com.brasfi.platforma.model.EixoTematico; // Import your EixoTematico enum if used in the form

public class TrilhaFormDTO {
    private Long id; // For editing existing trilhas
    @NotBlank(message = "O título da trilha é obrigatório.")
    @Size(max = 255, message = "O título da trilha não pode ter mais de 255 caracteres.")
    private String titulo;
    private String descricao; // Assuming this is also a form field
    private String topicosDeAprendizado; // Assuming this is also a form field
    private EixoTematico eixoTematico; // Assuming this is also a form field (e.g., dropdown)

    // Maps to @RequestParam("duracaoInput")
    @NotBlank(message = "A duração da trilha é obrigatória.")
    private String duracaoInput;

    // Maps to @RequestParam("capaFile")
    private MultipartFile capaFile;

    @Valid // Crucial for validating nested AulaFormDTOs
    private List<AulaFormDTO> aulas = new ArrayList<>(); // Matches the name attribute in your form

    // Getters and Setters for ALL fields (Spring data binding needs these)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTopicosDeAprendizado() { return topicosDeAprendizado; }
    public void setTopicosDeAprendizado(String topicosDeAprendizado) { this.topicosDeAprendizado = topicosDeAprendizado; }
    public EixoTematico getEixoTematico() { return eixoTematico; }
    public void setEixoTematico(EixoTematico eixoTematico) { this.eixoTematico = eixoTematico; }
    public String getDuracaoInput() { return duracaoInput; }
    public void setDuracaoInput(String duracaoInput) { this.duracaoInput = duracaoInput; }
    public MultipartFile getCapaFile() { return capaFile; }
    public void setCapaFile(MultipartFile capaFile) { this.capaFile = capaFile; }
    public List<AulaFormDTO> getAulas() { return aulas; }
    public void setAulas(List<AulaFormDTO> aulas) { this.aulas = aulas; }
}