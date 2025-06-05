package com.brasfi.platforma.controller;

import com.brasfi.platforma.config.UserDetailsImpl;
import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.model.EixoTematico;
import com.brasfi.platforma.model.Material;
import com.brasfi.platforma.model.Trilha;
import com.brasfi.platforma.repository.MaterialRepository;
import com.brasfi.platforma.service.AulaService;
import com.brasfi.platforma.service.TrilhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/trilhas")
public class TrilhaController {

    @Autowired
    private TrilhaService trilhaService;

    @Autowired
    private AulaService aulaService;

    @Autowired
    private MaterialRepository materialRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/registrar")
    public String mostrarRegistroTrilhaForm(Model model) {
        model.addAttribute("trilha", new Trilha());
        return "trilha/registrarTrilha";
    }

    @PostMapping("/registrar")
    public String registrarTrilha(
            @ModelAttribute Trilha trilha,
            @RequestParam("duracaoInput") String duracaoStr,
            @RequestParam("capaFile") MultipartFile capaFile,
            @RequestParam(value = "aulaTitulo", required = false) String aulaTitulo,
            @RequestParam(value = "linkAula", required = false) String linkAula,
            @RequestParam(value = "descricaoAula", required = false) String descricaoAula,
            @RequestParam(value = "documentosApoio", required = false) MultipartFile[] documentosApoio,
            @RequestParam(value = "linkApoio", required = false) String[] linkApoioAulas // Renamed for clarity vs. method param
    ) throws IOException {
        double duracao = parseDuracao(duracaoStr);
        trilha.setDuracao(duracao);
        if (capaFile != null && !capaFile.isEmpty()) {
            String nomeArquivo = System.currentTimeMillis() + "_" + capaFile.getOriginalFilename();
            Path pastaUploads = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(pastaUploads);
            Path destino = pastaUploads.resolve(nomeArquivo);
            capaFile.transferTo(destino.toFile());
            trilha.setCapa("/" + uploadDir + "/" + nomeArquivo);
        }
        if (trilha.getAulas() == null) {
            trilha.setAulas(new ArrayList<>());
        }
        Trilha trilhaSalva = trilhaService.salvarTrilha(trilha);

        if (aulaTitulo != null && !aulaTitulo.trim().isEmpty()) {
            Aula novaAula = new Aula();
            novaAula.setTitulo(aulaTitulo);
            novaAula.setLink(linkAula);
            novaAula.setDescricao(descricaoAula);

            if (novaAula.getTrilhas() == null) {
                novaAula.setTrilhas(new ArrayList<>());
            }
            if (novaAula.getMateriais() == null) {
                novaAula.setMateriais(new ArrayList<>());
            }
            novaAula.getTrilhas().add(trilhaSalva);

            trilhaSalva.getAulas().add(novaAula);

            Aula aulaSalva = aulaService.salvarAula(novaAula);

            if (documentosApoio != null && documentosApoio.length > 0) {
                for (MultipartFile arquivo : documentosApoio) {
                    if (arquivo != null && !arquivo.isEmpty()) {
                        try {
                            String nomeOriginal = arquivo.getOriginalFilename();
                            String aulaMaterialUploadDir = uploadDir + "/aulas/";
                            Path pastaUploadsAula = Paths.get(aulaMaterialUploadDir).toAbsolutePath().normalize();
                            Files.createDirectories(pastaUploadsAula);

                            Path destino = pastaUploadsAula.resolve(nomeOriginal);
                            arquivo.transferTo(destino.toFile());

                            Material material = new Material();
                            material.setNomeOriginal(nomeOriginal);
                            material.setCaminhoArquivo("/" + aulaMaterialUploadDir + nomeOriginal);
                            material.setAula(aulaSalva);
                            materialRepository.save(material);
                            System.out.println("DEBUG: Arquivo de aula salvo: " + nomeOriginal);
                        } catch (IOException e) {
                            System.err.println("ERRO: Falha ao salvar o arquivo de apoio da aula '" + arquivo.getOriginalFilename() + "': " + e.getMessage());
                        }
                    }
                }
            }

            if (linkApoioAulas != null && linkApoioAulas.length > 0) {
                for (String link : linkApoioAulas) {
                    if (link != null && !link.isBlank()) {
                        Material material = new Material();
                        material.setLinkApoio(link.trim());
                        material.setAula(aulaSalva);
                        materialRepository.save(material);
                        System.out.println("DEBUG: Link de aula salvo: " + link.trim());
                    }
                }
            }
        }
        return "redirect:/trilhas/listar";
    }

    private double parseDuracao(String duracaoStr) {
        if (duracaoStr == null || duracaoStr.isEmpty()) {
            throw new IllegalArgumentException("O campo de duração está vazio ou nulo.");
        }
        try {
            String[] partes = duracaoStr.split(":");
            if (partes.length != 2) {
                throw new IllegalArgumentException("Formato inválido para duração. Use hh:mm.");
            }
            int horas = Integer.parseInt(partes[0]);
            int minutos = Integer.parseInt(partes[1]);

            return horas + minutos / 60.0;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Não foi possível converter duração para número: " + duracaoStr, e);
        }
    }

    @GetMapping("/editar")
    public String mostrarEditarTrilhaForm(@RequestParam("id") Long id, Model model) {
        Trilha trilha = trilhaService.buscarPorId(id);

        double duracao = trilha.getDuracao();
        int horas = (int) duracao;
        int minutos = (int) Math.round((duracao - horas) * 60);
        String duracaoStr = String.format("%02d:%02d", horas, minutos);

        model.addAttribute("trilha", trilha);
        model.addAttribute("duracaoInput", duracaoStr);

        return "trilha/editarTrilha";
    }

    @PostMapping("/editar")
    public String editarTrilha(
            @ModelAttribute Trilha trilha,
            @RequestParam("duracaoInput") String duracaoStr,
            @RequestParam(value = "capaFile", required = false) MultipartFile capaFile
    ) throws IOException {

        double duracao = parseDuracao(duracaoStr);
        trilha.setDuracao(duracao);

        if (capaFile != null && !capaFile.isEmpty()) {
            String nomeArquivo = System.currentTimeMillis() + "_" + capaFile.getOriginalFilename();

            Path pastaUploads = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(pastaUploads);

            Path destino = pastaUploads.resolve(nomeArquivo);
            capaFile.transferTo(destino.toFile());

            trilha.setCapa("/" + uploadDir + "/" + nomeArquivo);
        } else {
            Trilha trilhaExistente = trilhaService.buscarPorId(trilha.getId());
            trilha.setCapa(trilhaExistente.getCapa());
        }

        trilhaService.atualizarTrilha(trilha);
        return "redirect:/trilhas/listar";
    }

    @GetMapping("/listar")
    public String mostrarListaTrilha(Model model) {
        List<Trilha> trilhas = trilhaService.listaTrilhas();
        model.addAttribute("trilhas", trilhas);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {

            Object principal = authentication.getPrincipal();

            String cargoUsuario = "CONVIDADO";

            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                cargoUsuario = userDetails.getUser().getTipoUsuario().toString();
            } else {
                System.out.println("Principal não é UserDetailsImpl, tipo: " + principal.getClass().getName());
            }

            model.addAttribute("cargoUsuario", cargoUsuario);
        } else {
            model.addAttribute("cargoUsuario", "CONVIDADO");
        }

        return "trilha/listarTrilha";
    }

    @GetMapping("/criar-modal")
    public String getCriarTrilhaModal(Model model) {
        model.addAttribute("trilha", new Trilha());
        model.addAttribute("eixosTematicos", EixoTematico.values());
        model.addAttribute("duracaoInput", "");
        return "trilha/registrarTrilha";
    }

    @GetMapping("/editar-modal/{id}")
    public String getEditarTrilhaModal(@PathVariable("id") Long id, Model model) {
        Trilha trilha = trilhaService.buscarPorId(id);

        double duracao = trilha.getDuracao();
        int horas = (int) duracao;
        int minutos = (int) Math.round((duracao - horas) * 60);
        String duracaoStr = String.format("%02d:%02d", horas, minutos);

        model.addAttribute("trilha", trilha);
        model.addAttribute("duracaoInput", duracaoStr);
        model.addAttribute("eixosTematicos", EixoTematico.values());

        return "trilha/editarTrilha :: modalContent";
    }

    @GetMapping("/deletar-modal/{id}")
    public String mostrarConfirmacao(@PathVariable("id") Long id, Model model) {
        Trilha trilha = trilhaService.buscarPorId(id);
        model.addAttribute("trilha", trilha);
        return "trilha/deletarTrilha :: modalContent";
    }

    @PostMapping("/deletar")
    public String deletarTrilha(Trilha trilha) {
        trilhaService.deletarTrilha(trilha);
        return "redirect:/trilhas/listar";
    }


    @GetMapping("/adicionar-aula")
    public String showAddAulaModal(Model model) {
        List<Trilha> todasAsTrilhas = trilhaService.listaTrilhas();
        model.addAttribute("trilhas", todasAsTrilhas);
        model.addAttribute("aula", new Aula());
        return "aula/adicionarAula :: modalContent";
    }

    @GetMapping("/{id}")
    public String mostrarDetalhesTrilha(@PathVariable Long id, Model model) {
        Trilha trilha = trilhaService.buscarPorId(id);
        if (trilha == null) {
            return "erro/404";
        }
        model.addAttribute("trilha", trilha);
        model.addAttribute("aulas", trilha.getAulas());
        return "trilha/detalheTrilha";
    }

}