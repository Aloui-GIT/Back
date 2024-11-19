package com.example.generateurformulaire.services;

import com.example.generateurformulaire.entities.Comment;
import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // Method to get all comments for a specific form by formId
    public List<Comment> getCommentsByFormId(Long formId) {
        return commentRepository.findByFormId(formId);
    }
    // Method to delete a comment by its ID
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
