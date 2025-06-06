package com.brasfi.platforma.controller;

import com.brasfi.platforma.config.UserDetailsImpl;
import com.brasfi.platforma.dto.AulaFormDTO;
import com.brasfi.platforma.dto.MaterialFormDTO;
import com.brasfi.platforma.dto.TrilhaFormDTO;
import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.model.EixoTematico;
import com.brasfi.platforma.model.Trilha;
import com.brasfi.platforma.repository.MaterialRepository; // MaterialRepository still here, but not used in this specific path
import com.brasfi.platforma.service.AulaService;
import com.brasfi.platforma.service.TrilhaService;
// Removed @Valid import as it's no longer directly on the list parameter
import jakarta.validation.ConstraintViolation; // Import for ConstraintViolation
import jakarta.validation.Valid;
import jakarta.validation.Validator; // Import for Validator
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError; // Import for FieldError
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set; // Used for Set<ConstraintViolation>


@Controller
@RequestMapping("/trilhas")
public class TrilhaController {

    @Autowired
    private TrilhaService trilhaService;

    @Autowired
    private AulaService aulaService;

    @Autowired
    private MaterialRepository materialRepository; // This autowired dependency is fine

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Inject the Validator for manual validation
    @Autowired
    private Validator validator;

    public TrilhaController(TrilhaService trilhaService, AulaService aulaService, Validator validator) {
        this.trilhaService = trilhaService;
        this.aulaService = aulaService;
        this.validator = validator;
    }

    @GetMapping("/registrar")
    public String mostrarRegistroTrilhaForm(Model model) {
        TrilhaFormDTO trilhaFormDTO = new TrilhaFormDTO();
        AulaFormDTO initialAula = new AulaFormDTO();
        initialAula.getMateriais().add(new MaterialFormDTO());
        trilhaFormDTO.getAulas().add(initialAula); // Add to the DTO's list
        model.addAttribute("trilhaFormDTO", trilhaFormDTO); // Add the single DTO to the model
        return "trilha/registrarTrilha";
    }

    @PostMapping("/registrar")
    public String registrarTrilha(
            @Valid @ModelAttribute TrilhaFormDTO trilhaFormDTO, // <--- Single DTO for all binding
            BindingResult bindingResult,
            Model model
    ) {
        // --- Manual Validation (if @Valid on TrilhaFormDTO itself is not sufficient or desired) ---
        // If you use @Valid on TrilhaFormDTO, Spring will populate bindingResult with errors
        // from TrilhaFormDTO, AulaFormDTO, and MaterialFormDTO automatically.
        // You might simplify or even remove this manual validation loop if Spring's
        // built-in validation is sufficient.

        // Example of adjusted manual validation:
        int aulaIndex = 0;
        // Line 81 in your original code, now potentially safer with a single DTO
        if (trilhaFormDTO.getAulas() != null) { // Add null check for safety
            for (AulaFormDTO aulaForm : trilhaFormDTO.getAulas()) {
                Set<ConstraintViolation<AulaFormDTO>> aulaViolations = validator.validate(aulaForm);
                for (ConstraintViolation<AulaFormDTO> violation : aulaViolations) {
                    String fieldName = "aulas[" + aulaIndex + "]." + violation.getPropertyPath().toString();
                    // Use "trilhaFormDTO" as the objectName to match the @ModelAttribute name
                    bindingResult.addError(new FieldError("trilhaFormDTO", fieldName, aulaForm, false, null, null, violation.getMessage()));
                }

                if (aulaForm.getMateriais() != null) {
                    int materialIndex = 0;
                    for (MaterialFormDTO materialForm : aulaForm.getMateriais()) {
                        if (materialForm.isEmpty()) {
                            materialIndex++;
                            continue;
                        }
                        Set<ConstraintViolation<MaterialFormDTO>> materialViolations = validator.validate(materialForm);
                        for (ConstraintViolation<MaterialFormDTO> violation : materialViolations) {
                            String fieldName = "aulas[" + aulaIndex + "].materiais[" + materialIndex + "]." + violation.getPropertyPath().toString();
                            bindingResult.addError(new FieldError("trilhaFormDTO", fieldName, materialForm, false, null, null, violation.getMessage()));
                        }
                        materialIndex++;
                    }
                }
                aulaIndex++;
            }
        }
        // --- END Manual Validation ---

        // Handle validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("trilhaFormDTO", trilhaFormDTO); // Repopulate form with submitted data
            model.addAttribute("mensagemErro", "Erro de validação. Por favor, verifique os campos destacados.");
            return "trilha/registrarTrilha"; // Return to the form view
        }

        try {
            // Parse duration
            double duracao = parseDuracao(trilhaFormDTO.getDuracaoInput());

            // Create and populate Trilha entity from TrilhaFormDTO
            Trilha trilha = new Trilha();
            trilha.setId(trilhaFormDTO.getId()); // For update scenarios
            trilha.setTitulo(trilhaFormDTO.getTitulo());
            trilha.setDescricao(trilhaFormDTO.getDescricao());
            trilha.setTopicosDeAprendizado(trilhaFormDTO.getTopicosDeAprendizado());
            trilha.setEixoTematico(trilhaFormDTO.getEixoTematico());
            trilha.setDuracao(duracao);

            // Capa upload logic
            if (trilhaFormDTO.getCapaFile() != null && !trilhaFormDTO.getCapaFile().isEmpty()) {
                String nomeArquivo = System.currentTimeMillis() + "_" + trilhaFormDTO.getCapaFile().getOriginalFilename();
                Path pastaUploads = Paths.get(uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(pastaUploads);
                Path destino = pastaUploads.resolve(nomeArquivo);
                trilhaFormDTO.getCapaFile().transferTo(destino.toFile());
                trilha.setCapa("/" + uploadDir + "/" + nomeArquivo);
            }

            // If Trilha has an 'aulas' field (List<Aula>), initialize it here
            // Note: You're currently using a ManyToMany for Trilha-Aula.
            // You'll likely build the list of Aula entities and then set it on Trilha.
            // Or let the aulaService handle the association.

            // Save the main Trilha entity first to get an ID if it's new
            Trilha trilhaSalva = trilhaService.salvarTrilha(trilha);

            // Process and save each aula and its materials
            if (trilhaFormDTO.getAulas() != null && !trilhaFormDTO.getAulas().isEmpty()) {
                for (AulaFormDTO aulaForm : trilhaFormDTO.getAulas()) {
                    if (aulaForm.isEmpty()) {
                        continue;
                    }

                    Aula novaAula = new Aula();
                    novaAula.setId(aulaForm.getId()); // For update scenarios
                    novaAula.setTitulo(aulaForm.getTitulo());
                    novaAula.setLink(aulaForm.getLink());
                    novaAula.setDescricao(aulaForm.getDescricao());

                    // Prepare documents and links from MaterialFormDTOs
                    List<MultipartFile> documentosDaAula = new ArrayList<>();
                    List<String> linksDaAula = new ArrayList<>();

                    if (aulaForm.getMateriais() != null) {
                        for (MaterialFormDTO materialForm : aulaForm.getMateriais()) {
                            if (materialForm.isEmpty()) {
                                continue;
                            }
                            if (materialForm.getDocumento() != null && !materialForm.getDocumento().isEmpty()) {
                                documentosDaAula.add(materialForm.getDocumento());
                            }
                            if (materialForm.getLinkApoio() != null && !materialForm.getLinkApoio().trim().isEmpty()) {
                                linksDaAula.add(materialForm.getLinkApoio());
                            }
                        }
                    }

                    // Call the service to save the aula and its materials, associating with trilhaSalva.getId()
                    aulaService.salvarAulaComTrilhaEspecifica(
                            novaAula,
                            trilhaSalva.getId(),
                            documentosDaAula.toArray(new MultipartFile[0]),
                            linksDaAula.toArray(new String[0])
                    );
                }
                model.addAttribute("mensagem", "Trilha e aulas salvas com sucesso!");
            } else {
                model.addAttribute("mensagem", "Trilha salva com sucesso! Nenhuma aula inicial adicionada.");
            }

            return "redirect:/trilhas/listar";
        } catch (IOException e) {
            model.addAttribute("mensagemErro", "Erro de I/O ao processar arquivos: " + e.getMessage());
            model.addAttribute("trilhaFormDTO", trilhaFormDTO); // Repopulate
            return "trilha/registrarTrilha";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("trilhaFormDTO", trilhaFormDTO); // Repopulate
            return "trilha/registrarTrilha";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensagemErro", "Ocorreu um erro inesperado: " + e.getMessage());
            model.addAttribute("trilhaFormDTO", trilhaFormDTO); // Repopulate
            return "trilha/registrarTrilha";
        }
    }


    private double parseDuracao(String duracaoStr) {
        if (duracaoStr == null || duracaoStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Duração não pode ser vazia.");
        }
        try {
            // If format is HH:mm
            if (duracaoStr.contains(":")) {
                String[] parts = duracaoStr.split(":");
                if (parts.length == 2) {
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1]);
                    return hours + (minutes / 60.0);
                }
            }
            // Fallback for decimal format (e.g., "1.5" or "1,5")
            String cleanedDuracao = duracaoStr.replace(',', '.');
            return Double.parseDouble(cleanedDuracao);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Duração inválida. Formato esperado HH:mm ou número decimal.");
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

    // Assuming this is the method your JS's fetch() hits for the modal HTML
    @GetMapping("/nova") // CHANGE THIS MAPPING
    public String loadNewTrilhaModalContent(Model model) {
        TrilhaFormDTO trilhaFormDTO = new TrilhaFormDTO();

        if (trilhaFormDTO.getAulas() == null) {
            trilhaFormDTO.setAulas(new ArrayList<>());
        }
        if (trilhaFormDTO.getAulas().isEmpty()) {
            AulaFormDTO initialAula = new AulaFormDTO();
            if (initialAula.getMateriais() == null) {
                initialAula.setMateriais(new ArrayList<>());
            }
            if (initialAula.getMateriais().isEmpty()) {
                initialAula.getMateriais().add(new MaterialFormDTO());
            }
            trilhaFormDTO.getAulas().add(initialAula);
        }

        model.addAttribute("trilhaFormDTO", trilhaFormDTO);
        return "trilha/registrarTrilha"; // The name of the HTML template for the modal
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