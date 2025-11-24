package com.cheburekmi.cheburek.repository;

import com.cheburekmi.cheburek.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByTelegramId(String telegramId);

    Optional<User> findByUserCode(String userCode);

    Boolean existsByUserCode(String userCode);
}
