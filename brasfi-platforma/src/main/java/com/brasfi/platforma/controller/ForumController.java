package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Forum;
import com.brasfi.platforma.model.Post;
import com.brasfi.platforma.repository.ForumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    private ForumRepository forumRepository;

    @GetMapping
    public Forum getForum() {
        return forumRepository.findById(1L).orElseThrow(); // único fórum
    }

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return forumRepository.findById(1L)
                .map(Forum::getPosts)
                .orElseThrow();
    }
}
