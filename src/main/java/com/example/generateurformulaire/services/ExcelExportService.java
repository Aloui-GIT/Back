package com.example.generateurformulaire.services;

import com.example.generateurformulaire.entities.Answer;
import com.example.generateurformulaire.entities.Submission;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
@Service
public class ExcelExportService {
    public ByteArrayInputStream exportToExcel(List<Submission> submissions) throws IOException {
        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Create a new sheet
            Sheet sheet = workbook.createSheet("Submissions");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = { "User Name", "Form Name", "Question", "Answer", "Time Spent (min)", "Submission Date" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]); // Set header cell value
            }

            // Populate rows with submission data
            int rowIdx = 1;
            for (Submission submission : submissions) {
                String userName = submission.getUser().getUsername();  // User name
                String formName = submission.getForm().getTitle();     // Form name
                int timeSpent = submission.getTimeSpent();             // Time spent
                String submissionDate = submission.getDateSubmission().toString(); // Submission date

                // For each answer in the submission, create a row
                for (Answer answer : submission.getAnswers()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(userName);
                    row.createCell(1).setCellValue(formName);
                    row.createCell(2).setCellValue(answer.getQuestion().getQuestion()); // Question text
                    row.createCell(3).setCellValue(answer.getAnswer());                    // User's answer
                    row.createCell(4).setCellValue(timeSpent);                             // Time spent
                    row.createCell(5).setCellValue(submissionDate);                        // Submission date
                }
            }

            // Write workbook content to output stream
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } finally {
            workbook.close();
            out.close();
        }
    }
}
