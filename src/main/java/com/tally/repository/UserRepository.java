package com.tally.repository;

import com.tally.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndLicenceNo(String email, Long licenceNo);
    List<User> findAllByEmail(String email);
    User findByMobile(String mobile);
}
