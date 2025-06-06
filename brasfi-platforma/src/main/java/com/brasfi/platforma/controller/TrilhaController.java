package com.brasfi.platforma.controller;

import com.brasfi.platforma.config.UserDetailsImpl;
import com.brasfi.platforma.dto.AulaFormDTO;
import com.brasfi.platforma.dto.MaterialFormDTO;
import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.model.EixoTematico;
import com.brasfi.platforma.model.Trilha;
import com.brasfi.platforma.repository.MaterialRepository; // MaterialRepository still here, but not used in this specific path
import com.brasfi.platforma.service.AulaService;
import com.brasfi.platforma.service.TrilhaService;
// Removed @Valid import as it's no longer directly on the list parameter
import jakarta.validation.ConstraintViolation; // Import for ConstraintViolation
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

    @GetMapping("/registrar")
    public String mostrarRegistroTrilhaForm(Model model) {
        model.addAttribute("trilha", new Trilha());
        List<AulaFormDTO> aulas = new ArrayList<>();
        AulaFormDTO initialAula = new AulaFormDTO();
        initialAula.getMateriais().add(new MaterialFormDTO()); // Add an initial material for the first aula
        aulas.add(initialAula);
        model.addAttribute("aulas", aulas);
        return "trilha/registrarTrilha";
    }

    @PostMapping("/registrar")
    public String registrarTrilha(
            @ModelAttribute Trilha trilha,
            @RequestParam("duracaoInput") String duracaoStr,
            @RequestParam("capaFile") MultipartFile capaFile,
            @ModelAttribute("aulas") List<AulaFormDTO> aulasForms, // REMOVED @Valid here
            BindingResult bindingResult,
            Model model
    ) {
        // --- START: Manual Validation for aulasForms ---
        // Removed the problematic line: bindingResult.getFieldErrors("aulas").forEach(bindingResult::removeBindingResult);
        // Spring's BindingResult will typically not have detailed validation errors for the list itself
        // when @Valid is removed. We are now explicitly adding them.

        int aulaIndex = 0;
        for (AulaFormDTO aulaForm : aulasForms) {
            // Validate each AulaFormDTO instance
            Set<ConstraintViolation<AulaFormDTO>> aulaViolations = validator.validate(aulaForm);
            for (ConstraintViolation<AulaFormDTO> violation : aulaViolations) {
                // Construct the field name to correctly map to Thymeleaf's expected format (e.g., "aulas[0].titulo")
                String fieldName = "aulas[" + aulaIndex + "]." + violation.getPropertyPath().toString();
                bindingResult.addError(new FieldError("aulas", fieldName, aulaForm, false, null, null, violation.getMessage()));
            }

            // Also validate nested MaterialFormDTOs
            if (aulaForm.getMateriais() != null) {
                int materialIndex = 0;
                for (MaterialFormDTO materialForm : aulaForm.getMateriais()) {
                    // IMPORTANT: Filter out completely empty material DTOs before validating them.
                    // This prevents validation errors on "empty" rows that the user might have left blank.
                    // The 'isEmpty()' check in MaterialFormDTO determines if both link and document are empty.
                    if (materialForm.isEmpty()) {
                        materialIndex++;
                        continue; // Skip completely empty material blocks
                    }

                    Set<ConstraintViolation<MaterialFormDTO>> materialViolations = validator.validate(materialForm);
                    for (ConstraintViolation<MaterialFormDTO> violation : materialViolations) {
                        String fieldName = "aulas[" + aulaIndex + "].materiais[" + materialIndex + "]." + violation.getPropertyPath().toString();
                        bindingResult.addError(new FieldError("aulas", fieldName, materialForm, false, null, null, violation.getMessage()));
                    }
                    materialIndex++;
                }
            }
            aulaIndex++;
        }
        // --- END: Manual Validation ---

        // Handle validation errors (including those added manually)
        if (bindingResult.hasErrors()) {
            model.addAttribute("trilha", trilha);
            model.addAttribute("aulas", aulasForms); // Repopulate form with submitted data
            model.addAttribute("mensagemErro", "Erro de validação. Por favor, verifique os campos destacados.");
            // In a real application, you'd map these errors to specific fields for better UX
            return "trilha/registrarTrilha";
        }

        try {
            double duracao = parseDuracao(duracaoStr);
            trilha.setDuracao(duracao);

            // Capa upload logic
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

            // Process and save each aula and its materials
            if (aulasForms != null && !aulasForms.isEmpty()) {
                for (AulaFormDTO aulaForm : aulasForms) {
                    // Skip completely empty aula blocks (e.g., if user added and then cleared inputs)
                    if (aulaForm.isEmpty()) {
                        continue;
                    }
                    // This check for title/link being empty is a fallback.
                    // The @NotBlank on AulaFormDTO should ideally catch this first via manual validation.
                    if (aulaForm.getTitulo() == null || aulaForm.getTitulo().trim().isEmpty() ||
                            aulaForm.getLink() == null || aulaForm.getLink().trim().isEmpty()) {
                        model.addAttribute("mensagemErro", "Erro: Título e link são obrigatórios para todas as aulas.");
                        model.addAttribute("trilha", trilha);
                        model.addAttribute("aulas", aulasForms);
                        return "trilha/registrarTrilha";
                    }

                    Aula novaAula = new Aula();
                    novaAula.setId(aulaForm.getId()); // For update scenarios
                    novaAula.setTitulo(aulaForm.getTitulo());
                    novaAula.setLink(aulaForm.getLink());
                    novaAula.setDescricao(aulaForm.getDescricao());

                    // Filter and prepare documents and links from MaterialFormDTOs
                    List<MultipartFile> documentosDaAula = new ArrayList<>();
                    List<String> linksDaAula = new ArrayList<>();

                    if (aulaForm.getMateriais() != null) {
                        for (MaterialFormDTO materialForm : aulaForm.getMateriais()) {
                            // Filter out completely empty material blocks here again before saving.
                            // This ensures that only relevant data is passed to the service layer.
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

                    // Call the service to save the aula and its materials
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
            model.addAttribute("trilha", trilha);
            model.addAttribute("aulas", aulasForms);
            return "trilha/registrarTrilha";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("trilha", trilha);
            model.addAttribute("aulas", aulasForms);
            return "trilha/registrarTrilha";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensagemErro", "Ocorreu um erro inesperado: " + e.getMessage());
            model.addAttribute("trilha", trilha);
            model.addAttribute("aulas", aulasForms);
            return "trilha/registrarTrilha";
        }
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