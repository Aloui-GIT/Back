package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.DTO.SubmissionDetailsDto;
import com.example.generateurformulaire.DTO.SubmissionDto;
import com.example.generateurformulaire.DTO.SubmissionSummary;
import com.example.generateurformulaire.entities.Answer;
import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.entities.Question;
import com.example.generateurformulaire.entities.Submission;
import com.example.generateurformulaire.repository.AnswerRepository;
import com.example.generateurformulaire.repository.QuestionRepository;
import com.example.generateurformulaire.services.AnswerService;
import com.example.generateurformulaire.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/submissions")
@CrossOrigin(origins = "*")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private AnswerService answerService ;
    @Autowired
    private AnswerRepository answerRepository ;
    @Autowired
    private QuestionRepository questionRepository ;

    @GetMapping
    public ResponseEntity<List<Submission>> getAllSubmissions() {
        List<Submission> submissions = submissionService.getAllSubmissions();
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @PostMapping("/createSubmission")
    public ResponseEntity<Submission> createSubmission(
            @RequestParam Long userId,
            @RequestParam Long formId) {

        try {
            // Validate userId and formId (e.g., check if they are positive)
            if (userId == null || formId == null || userId <= 0 || formId <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Create the submission
            Submission submission = submissionService.createSubmission(userId, formId);

            // Return the created submission with HTTP 201 Created status
            return new ResponseEntity<>(submission, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Return HTTP 400 Bad Request if user or form is not found
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Return HTTP 500 Internal Server Error for unexpected issues
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getSubmission/user/{userId}/form/{formId}")
    public ResponseEntity<Submission> getSubmission(@PathVariable Long userId, @PathVariable Long formId) {
        return submissionService.getSubmissionByUserIdAndFormId(userId, formId)
                .map(submission -> new ResponseEntity<>(submission, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/updateSubmission/{submissionId}")
    public ResponseEntity<Submission> updateSubmission(@PathVariable Long submissionId,
                                                       @RequestBody SubmissionDto submissionDto) {
        Submission updatedSubmission = submissionService.updateSubmission(submissionId, submissionDto);
        return new ResponseEntity<>(updatedSubmission, HttpStatus.OK);
    }
  @PostMapping("/saveSubmission")
   public ResponseEntity<Object> saveSubmission(@RequestBody SubmissionDto submissionDto) {
       try {
           submissionService.saveSubmission(submissionDto);
           return ResponseEntity.ok().body(createResponse("Submission saved successfully", HttpStatus.OK.value()));
       } catch (IllegalArgumentException e) {
           return ResponseEntity.badRequest().body(createResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResponse("An error occurred while saving the submission", HttpStatus.INTERNAL_SERVER_ERROR.value()));
       }
   }

    private Map<String, Object> createResponse(String message, int status) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", status);
        return response;
    }

    @GetMapping("/details")
    public ResponseEntity<List<SubmissionDetailsDto>> getAllSubmissionDetails() {
        // Fetch all submissions
        List<Submission> submissions = submissionService.getAllSubmissions();

        // Create a map to cache questions for efficiency
        Map<Long, Question> questionCache = new HashMap<>();

        // Create a list to hold all submission details
        List<SubmissionDetailsDto> submissionDetails = new ArrayList<>();

        // Iterate through each submission
        for (Submission submission : submissions) {
            // Fetch answers for the current submission
            List<Answer> answers = answerService.getAnswersBySubmissionId(submission.getIdSubmission());

            // Extract question IDs from answers
            List<Long> questionIds = answers.stream()
                    .map(answer -> answer.getQuestion().getIdQuestion())
                    .distinct()
                    .collect(Collectors.toList());

            // Fetch questions based on extracted IDs if not already cached
            List<Question> questions = new ArrayList<>();
            for (Long questionId : questionIds) {
                if (!questionCache.containsKey(questionId)) {
                    Question question = questionRepository.findById(questionId).orElse(null);
                    if (question != null) {
                        questionCache.put(questionId, question);
                        questions.add(question);
                    }
                }
            }

            // Replace question references in answers with the full question objects
            answers.forEach(answer -> {
                Question question = questionCache.get(answer.getQuestion().getIdQuestion());
                answer.setQuestion(question);
            });

            // Create a DTO for the current submission
            SubmissionDetailsDto dto = new SubmissionDetailsDto();
            dto.setIdSubmission(submission.getIdSubmission());
            dto.setDateSubmission(submission.getDateSubmission());
            dto.setUser(submission.getUser());
            dto.setForm(submission.getForm());

            dto.setAnswers(answers);
            dto.setQuestions(questions); // Set the questions in DTO

            // Add the DTO to the list
            submissionDetails.add(dto);
        }

        return ResponseEntity.ok(submissionDetails);
    }


    @GetMapping("/{submissionId}/forms")
    public Set<Form> getFormsBySubmissionId(@PathVariable Long submissionId) {
        return submissionService.getFormsBySubmissionId(submissionId);
    }


    @GetMapping("/form/{formId}")
    public List<Submission> getSubmissionsByFormId(@PathVariable Long formId) {
        return submissionService.getSubmissionsByFormId(formId);
    }

    @GetMapping("/getFormAnalytics/form/{formId}")
    public Map<String, Object> getFormAnalytics(@PathVariable Long formId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalSubmissions", submissionService.getTotalSubmissions(formId));
        analytics.put("averageResponseTime", submissionService.getAverageResponseTime(formId));
        analytics.put("completionRate", submissionService.getCompletionRate(formId));
        return analytics;
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Submission>> getSubmissionHistoryByUserId(@PathVariable Long userId) {
        List<Submission> history = submissionService.getSubmissionHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }


    @GetMapping("/counts")
    public ResponseEntity<List<SubmissionSummary>> getSubmissionCounts() {
        List<SubmissionSummary> submissionCounts = submissionService.getSubmissionCounts();
        return ResponseEntity.ok(submissionCounts);
    }

    @GetMapping("/countsubmission")
    public ResponseEntity<Integer> getSubmissionCount(@RequestParam Long userId, @RequestParam Long formId) {
        int count = submissionService.getSubmissionCountByUserAndForm(userId, formId);
        return ResponseEntity.ok(count);
    }
}