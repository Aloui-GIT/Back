package com.example.generateurformulaire.repository;

import com.example.generateurformulaire.entities.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByTemplate(boolean template);
    @Query("SELECT f FROM Form f WHERE f.user.userId = :userId")
    List<Form> findByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Form f WHERE " +
            "LOWER(f.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.Description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.user.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(f.CreateDate AS string) LIKE CONCAT('%', :query, '%')")
    List<Form> searchFormsAcrossFields(@Param("query") String query);


}
