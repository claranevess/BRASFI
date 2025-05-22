package com.brasfi.platforma.dto;

import org.springframework.web.multipart.MultipartFile;

public class TrilhaFormDTO {
    private String titulo;
    private String descricao;
    private MultipartFile capaFile; // Arquivo enviado no formulário
    private Integer duracaoHoras;
    private Integer duracaoMinutos;
    private String topicosDeAprendizado;
    private String eixoTematico;  // String para facilitar seleção do ENUM no front

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public MultipartFile getCapaFile() {
        return capaFile;
    }

    public void setCapaFile(MultipartFile capaFile) {
        this.capaFile = capaFile;
    }

    public Integer getDuracaoHoras() {
        return duracaoHoras;
    }

    public void setDuracaoHoras(Integer duracaoHoras) {
        this.duracaoHoras = duracaoHoras;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public String getTopicosDeAprendizado() {
        return topicosDeAprendizado;
    }

    public void setTopicosDeAprendizado(String topicosDeAprendizado) {
        this.topicosDeAprendizado = topicosDeAprendizado;
    }

    public String getEixoTematico() {
        return eixoTematico;
    }

    public void setEixoTematico(String eixoTematico) {
        this.eixoTematico = eixoTematico;
    }
}
