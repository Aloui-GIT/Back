package com.example.generateurformulaire.repository;

import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SubmissionRepository  extends JpaRepository<Submission, Long> {
    @Query("SELECT s FROM Submission s WHERE s.user.userId = :userId AND s.form.idForm = :formId")
    Optional<Submission> findByUserIdAndFormId(@Param("userId") Long userId, @Param("formId") Long formId);

    @Query("SELECT s.form FROM Submission s WHERE s.idSubmission = :submissionId")
    Set<Form> findFormsBySubmissionId(@Param("submissionId") Long submissionId);


    @Query("SELECT s FROM Submission s WHERE s.form.idForm = :formId")
    List<Submission> findByFormId(Long formId);


    @Query("SELECT COUNT(s) FROM Submission s WHERE s.form.idForm = :formId")
    long countSubmissionsByFormId(@Param("formId") Long formId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.form.idForm = :formId AND s.endTime IS NOT NULL")
    long countCompletedSubmissionsByFormId(@Param("formId") Long formId);

    @Query("SELECT s FROM Submission s WHERE s.form.idForm = :formId")
    List<Submission> findAllByFormId(@Param("formId") Long formId);
    @Query("SELECT s FROM Submission s WHERE s.user.userId = :userId")
    List<Submission> findByUserId(Long userId);
    @Query("SELECT s.form.idForm, COUNT(s) FROM Submission s GROUP BY s.form.idForm")
    List<Object[]> countSubmissionsByForm();

    @Query(value = "SELECT COUNT(*) FROM Submission WHERE user_id = :userId AND form_id = :formId", nativeQuery = true)
    int countByUserIdAndFormId(@Param("userId") Long userId, @Param("formId") Long formId);
}
