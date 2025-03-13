package com.example.KayakBooking.repository;

import com.example.KayakBooking.model.UsersEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByUsername(String username);

    @Query("SELECT i FROM UsersEntity i WHERE i.reset = true")
    List<UsersEntity> findNoSend();

    @Modifying
    @Transactional
    @Query("UPDATE UsersEntity i SET i.reset = :status WHERE i.username = :username")
    void updateEmailSendStatusByEmail(@Param("username") String email, @Param("status") boolean status);
}