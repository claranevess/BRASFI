package com.brasfi.platforma.service;

import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.model.Comentario;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    public List<Comentario> getCommentsForAula(Long aulaId) {
        return comentarioRepository.findByAulaIdAndParentComentarioIsNullOrderByDataCriacaoAsc(aulaId);
    }

    public List<Comentario> getRepliesForComment(Comentario parentComentario) {
        return comentarioRepository.findByParentComentarioOrderByDataCriacaoAsc(parentComentario);
    }

    public Comentario saveComment(String texto, User user, Aula aula) {
        Comentario comentario = new Comentario();
        comentario.setTexto(texto);
        comentario.setUser(user);
        comentario.setAula(aula);
        comentario.setDataCriacao(LocalDateTime.now());
        return comentarioRepository.save(comentario);
    }

    public Comentario saveReply(String texto, User user, Aula aula, Comentario parentComentario) {
        Comentario reply = new Comentario();
        reply.setTexto(texto);
        reply.setUser(user);
        reply.setAula(aula);
        reply.setParentComentario(parentComentario);
        reply.setDataCriacao(LocalDateTime.now());
        return comentarioRepository.save(reply);
    }

    public Comentario getCommentById(Long id) {
        return comentarioRepository.findById(id).orElse(null);
    }
}