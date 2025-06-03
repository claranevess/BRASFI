package com.brasfi.platforma.service;

import com.brasfi.platforma.model.*;
import com.brasfi.platforma.repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;

    public Mensagem enviarMensagem(User user, Grupo grupo, String texto) {
        Mensagem mensagem = new Mensagem();
        mensagem.setUser(user);
        mensagem.setGrupo(grupo);
        mensagem.setMessage(texto);
        mensagem.setDataCriacao(LocalDate.now());

        return mensagemRepository.save(mensagem);
    }

    public List<Mensagem> listarMensagensPorGrupo(Grupo grupo) {
        return mensagemRepository.findByGrupoOrderByDataCriacaoAsc(grupo);
    }
}

