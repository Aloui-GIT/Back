package com.example.generateurformulaire.Controllers;

import com.example.generateurformulaire.entities.Submission;
import com.example.generateurformulaire.services.ExcelExportService;
import com.example.generateurformulaire.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "*")
public class ExportController {
    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private  SubmissionService submissionService; // To fetch submissions


    @GetMapping("/answers")
    public ResponseEntity<InputStreamResource> exportSubmissionsToExcel() throws IOException {
        List<Submission> submissions = submissionService.getAllSubmissions(); // Fetch submissions
        ByteArrayInputStream excelFile = excelExportService.exportToExcel(submissions);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=submissions.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(excelFile));
    }
}
