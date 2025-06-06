package com.brasfi.platforma.service;

import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.model.Material;
import com.brasfi.platforma.model.Trilha;
import com.brasfi.platforma.repository.AulaRepository;
import com.brasfi.platforma.repository.MaterialRepository;
import com.brasfi.platforma.repository.TrilhaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AulaService {
    private final AulaRepository aulaRepository;
    private final MaterialRepository materialRepository;
    private final TrilhaRepository trilhaRepository;
    private final String uploadDir = "uploads"; // Diretório base para uploads

    public AulaService(AulaRepository aulaRepository, MaterialRepository materialRepository, TrilhaRepository trilhaRepository) {
        this.aulaRepository = aulaRepository;
        this.materialRepository = materialRepository;
        this.trilhaRepository = trilhaRepository;
    }

    public Aula salvarAula(Aula aula) {
        return aulaRepository.save(aula);
    }

    public Aula buscarPorId(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aula não encontrada com ID: " + id));
    }

    public List<Aula> listarTodas() {
        return aulaRepository.findAll();
    }

    public boolean marcarComoConcluida(Long id) {
        Optional<Aula> optionalAula = aulaRepository.findById(id);
        if (optionalAula.isPresent()) {
            Aula aula = optionalAula.get();
            aula.setConcluida(true);
            aulaRepository.save(aula);
            return true;
        }
        return false;
    }

    public Aula salvarAulaComTrilhaEspecifica(
            Aula aula,
            Long trilhaId,
            MultipartFile[] documentos,
            String[] linksApoio) throws IOException {

        Trilha trilha = trilhaRepository.findById(trilhaId)
                .orElseThrow(() -> new IllegalArgumentException("Trilha com ID " + trilhaId + " não encontrada."));

        if (aula.getTrilhas() == null) {
            aula.setTrilhas(new ArrayList<>());
        }
        if (aula.getMateriais() == null) {
            aula.setMateriais(new ArrayList<>());
        }

        // Associate aula with trilha
        aula.getTrilhas().add(trilha);
        Aula aulaSalva = aulaRepository.save(aula);

        // Update trilha with new aula (important for bidirectional relationship management)
        if (trilha.getAulas() == null) {
            trilha.setAulas(new ArrayList<>());
        }
        trilha.getAulas().add(aulaSalva);
        trilhaRepository.save(trilha); // Save trilha to update its list of aulas

        // Save documents
        if (documentos != null && documentos.length > 0) {
            String aulaMaterialUploadPath = uploadDir + "/aulas/" + aulaSalva.getId() + "/";
            Path pastaUpload = Paths.get(aulaMaterialUploadPath).toAbsolutePath().normalize();
            Files.createDirectories(pastaUpload);

            for (MultipartFile documento : documentos) {
                if (!documento.isEmpty()) {
                    String nomeArquivo = System.currentTimeMillis() + "_" + documento.getOriginalFilename();
                    Path destino = pastaUpload.resolve(nomeArquivo);
                    documento.transferTo(destino.toFile());

                    Material material = new Material();
                    material.setNomeOriginal(documento.getOriginalFilename());
                    material.setCaminhoArquivo("/" + aulaMaterialUploadPath + nomeArquivo); // Store relative path
                    // REMOVED: material.setTipo("documento");
                    material.setAula(aulaSalva);
                    aulaSalva.getMateriais().add(material);
                }
            }
        }

        // Save links
        if (linksApoio != null && linksApoio.length > 0) {
            for (String link : linksApoio) {
                if (link != null && !link.trim().isEmpty()) {
                    Material material = new Material();
                    material.setLinkApoio(link);
                    // REMOVED: material.setTipo("link");
                    material.setAula(aulaSalva);
                    aulaSalva.getMateriais().add(material);
                }
            }
        }
        aulaRepository.save(aulaSalva);

        return aulaSalva;
    }


}
