package com.example.generateurformulaire.AppUser;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
    Optional<User> findByUsername(String username);


    Optional<User> findByEmail(String email);

    @Modifying
    @Query("update User set role = :role where username = :username")
    void updateUserRole(@Param("username") String username, @Param("role") AppUserRole role);
    
    @Modifying
    @Query("update User set role = 'ADMIN' where username = :username")
    void makeAdmin(@Param("username") String username);




    Optional<User> findByVerificationToken(String token);

    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    User getUserById(@Param("userId") Long userId);



    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.role) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchUsersAcrossFields(@Param("query") String query);

}
