package com.spring.MySpring.repo;

import com.spring.MySpring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    User findByActivationCode(String code);
}
