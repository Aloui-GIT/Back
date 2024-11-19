package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.entities.Comment;
import com.example.generateurformulaire.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")

public class CommentController {
    @Autowired
    private CommentService commentService;

    // Endpoint to get all comments for a specific form by formId
    @GetMapping("/getCommentsByForm/{formId}")
    public List<Comment> getCommentsByForm(@PathVariable Long formId) {
        return commentService.getCommentsByFormId(formId);
    }

    // Endpoint to delete a comment by its ID
    @DeleteMapping("/deleteComment/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
