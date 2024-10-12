package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.DTO.AnswerDto;
import com.example.generateurformulaire.entities.Answer;
import com.example.generateurformulaire.entities.Options;
import com.example.generateurformulaire.entities.Question;
import com.example.generateurformulaire.services.AnswerService;
import com.example.generateurformulaire.services.OptionsService;
import com.example.generateurformulaire.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/answers")
@CrossOrigin(origins = "*")
public class AnswerController {

    @Autowired
    private AnswerService answerService;


    @GetMapping
    public ResponseEntity<List<Answer>> getAllAnswers() {
        List<Answer> answers = answerService.getAllAnswers();
        return new ResponseEntity<>(answers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Answer> getAnswerById(@PathVariable("id") Long id) {
        return answerService.getAnswerById(id)
                .map(answer -> new ResponseEntity<>(answer, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


  @PostMapping("/createAnswers")
  public ResponseEntity<List<Answer>> createAnswers(@RequestBody List<Answer> answers) {
      try {
          List<Answer> createdAnswers = answerService.createAnswers(answers);
          return new ResponseEntity<>(createdAnswers, HttpStatus.CREATED);
      } catch (IllegalArgumentException e) {
          return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
      }
  }

    @PutMapping("/{id}")
    public ResponseEntity<Answer> updateAnswer(@PathVariable("id") Long id, @RequestBody Answer answer) {
        if (!answerService.getAnswerById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        answer.setIdAnswer(id);
        Answer updatedAnswer = answerService.updateAnswer(answer);
        return new ResponseEntity<>(updatedAnswer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable("id") Long id) {
        answerService.deleteAnswerById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @PostMapping("/addAnswerToSubmission")
    public ResponseEntity<Answer> addAnswerToSubmission(
            @RequestParam Long submissionId,
            @RequestParam Long questionId,
            @RequestParam String answerText) {

        try {
            Answer answer = answerService.addAnswerToSubmission(submissionId, questionId, answerText);
            return new ResponseEntity<>(answer, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint to update an existing answer
    @PutMapping("/update/{id}")
    public ResponseEntity<Answer> updateAnswer(
            @PathVariable Long id,
            @RequestParam String answerText) {

        try {
            Answer answer = answerService.updateAnswer(id, answerText);
            return new ResponseEntity<>(answer, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



    @PutMapping("/{answerId}/assign-option/{optionId}")
    public ResponseEntity<String> assignOptionToAnswer(
            @PathVariable Long answerId,
            @PathVariable Long optionId) {
        try {
            answerService.assignOptionToAnswer(answerId, optionId);
            return ResponseEntity.ok("Option assigned to answer successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }



    @GetMapping("/getAnswersByQuestionId/{questionId}")
    public ResponseEntity<List<Answer>> getAnswersByQuestionId(@PathVariable Long questionId) {
        List<Answer> answers = answerService.getAnswersByQuestionId(questionId);
        return ResponseEntity.ok(answers);
    }


    @GetMapping("/submission/{submissionId}")
    public ResponseEntity<List<Answer>> getAnswersBySubmissionId(@PathVariable Long submissionId) {
        List<Answer> answers = answerService.getAnswersBySubmissionId(submissionId);
        if (answers.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 if no answers found
        }
        return ResponseEntity.ok(answers);
    }
}