package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.model.Comentario;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.service.AulaService;
import com.brasfi.platforma.service.ComentarioService;
import com.brasfi.platforma.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController // Changed to RestController for API endpoints
@RequestMapping("/api/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;
    @Autowired
    private UserService userService; // Assuming you have a UserService
    @Autowired
    private AulaService aulaService; // Assuming you have an AulaService

    // DTO for returning comment data, including replies
    public static class ComentarioDTO {
        public Long id;
        public String texto;
        public String dataCriacao;
        public String autorNome;
        public List<ComentarioDTO> replies;
    }

    private ComentarioDTO convertToDTO(Comentario comentario) {
        ComentarioDTO dto = new ComentarioDTO();
        dto.id = comentario.getId();
        dto.texto = comentario.getTexto();
        dto.dataCriacao = comentario.getDataCriacao().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy 'às' HH:mm"));
        dto.autorNome = comentario.getUser().getNome(); // Assuming User has a getNome() method
        if (comentario.getReplies() != null && !comentario.getReplies().isEmpty()) {
            dto.replies = comentario.getReplies().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        return dto;
    }

    @GetMapping("/aula/{aulaId}")
    public ResponseEntity<List<ComentarioDTO>> getCommentsForAula(@PathVariable Long aulaId) {
        List<Comentario> comments = comentarioService.getCommentsForAula(aulaId);
        List<ComentarioDTO> commentDTOs = comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }

    @PostMapping("/aula/{aulaId}")
    public ResponseEntity<ComentarioDTO> addCommentToAula(@PathVariable Long aulaId, @RequestBody Map<String, String> payload, Principal principal) {
        // In a real application, you'd get the authenticated user from `principal`
        // For now, let's assume a dummy user or retrieve from a service
        User currentUser = userService.getUserByUsername(principal.getName()); // Or fetch by ID, etc.
        if (currentUser == null) {
            // Handle case where user is not found (e.g., return unauthorized)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String texto = payload.get("texto");
        Aula aula = aulaService.buscarPorId(aulaId);

        if (aula == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Comentario newComment = comentarioService.saveComment(texto, currentUser, aula);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(newComment));
    }

    @PostMapping("/{parentComentarioId}/reply")
    public ResponseEntity<ComentarioDTO> addReplyToComment(@PathVariable Long parentComentarioId, @RequestBody Map<String, String> payload, Principal principal) {
        User currentUser = userService.getUserByUsername(principal.getName()); // Or fetch by ID, etc.
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String texto = payload.get("texto");
        Comentario parentComentario = comentarioService.getCommentById(parentComentarioId);

        if (parentComentario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Comentario newReply = comentarioService.saveReply(texto, currentUser, parentComentario.getAula(), parentComentario);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(newReply));
    }
}