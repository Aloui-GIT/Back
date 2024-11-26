package com.example.generateurformulaire.repository;

import com.example.generateurformulaire.entities.Form;
import com.example.generateurformulaire.entities.LikeDislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface LikeDislikeRepository extends JpaRepository<LikeDislike, Long> {
    @Query("SELECT ld FROM LikeDislike ld WHERE ld.form.idForm = :formId AND ld.user.userId = :userId")
    Optional<LikeDislike> findByFormIdAndUserId(@Param("formId") Long formId, @Param("userId") Long userId);



    @Query("SELECT COUNT(ld) FROM LikeDislike ld WHERE ld.form = :form AND ld.isLike = true")
    int countLikesByForm(@Param("form") Form form);

    @Query("SELECT COUNT(ld) FROM LikeDislike ld WHERE ld.form = :form AND ld.isLike = false")
    int countDislikesByForm(@Param("form") Form form);


}
