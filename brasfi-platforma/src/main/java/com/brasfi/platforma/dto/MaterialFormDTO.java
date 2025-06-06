package com.brasfi.platforma.dto;

import jakarta.validation.constraints.AssertTrue;
// import lombok.Data; // REMOVE THIS LINE
import org.springframework.web.multipart.MultipartFile;

public class MaterialFormDTO { // Change from @Data to plain class

    private Long id; // Keep this if you intend to use it for editing, otherwise remove
    private String linkApoio;
    private MultipartFile documento;

    // Manually add Getters and Setters:
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } // And this setter

    public String getLinkApoio() { return linkApoio; }
    public void setLinkApoio(String linkApoio) { this.linkApoio = linkApoio; }

    public MultipartFile getDocumento() { return documento; }
    public void setDocumento(MultipartFile documento) { this.documento = documento; }

    @AssertTrue(message = "Cada material deve ter um link de apoio ou um documento anexado.")
    public boolean hasContent() {
        boolean hasLink = linkApoio != null && !linkApoio.trim().isEmpty();
        boolean hasDocument = (documento != null && !documento.isEmpty());
        return hasLink || hasDocument;
    }

    public boolean isEmpty() {
        return (linkApoio == null || linkApoio.trim().isEmpty()) &&
                (documento == null || documento.isEmpty());
    }
}