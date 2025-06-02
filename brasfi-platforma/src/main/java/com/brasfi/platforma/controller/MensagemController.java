package com.brasfi.platforma.controller;

import com.brasfi.platforma.dto.NovaMensagemDTO;
import com.brasfi.platforma.model.Grupo;
import com.brasfi.platforma.model.Mensagem;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.repository.GrupoRepository;
import com.brasfi.platforma.repository.MensagemRepository;
import com.brasfi.platforma.repository.UserRepository;
import com.brasfi.platforma.service.MensagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mensagens")
public class MensagemController {
    @Autowired private MensagemService mensagemService;
    @Autowired private UserRepository userRepository;
    @Autowired private GrupoRepository grupoRepository;
    @Autowired private MensagemRepository mensagemRepository; // Adicione esta linha

    @GetMapping("/mensagem")
    public String mostrarMensagem() {
        return "mensagem";
    }

    @PostMapping("/enviar")
    public ResponseEntity<Mensagem> enviarMensagem(@RequestBody NovaMensagemDTO dto) {
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        Optional<Grupo> grupoOpt = grupoRepository.findById(dto.getGrupoId());

        if (userOpt.isEmpty() || grupoOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Mensagem mensagem = new Mensagem();
        mensagem.setTexto(dto.getTexto());
        mensagem.setUser(userOpt.get());
        mensagem.setGrupo(grupoOpt.get());
        mensagem.setDataCriacao(LocalDate.from(LocalDateTime.now()));

        mensagemRepository.save(mensagem);

        return ResponseEntity.ok(mensagem);
    }

    @GetMapping("/grupo/{id}")
    public ResponseEntity<List<Mensagem>> mensagensGrupo(@PathVariable Long id) {
        Grupo grupo = grupoRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(mensagemService.listarMensagensPorGrupo(grupo));
    }
}

