package com.ermnvldmr.w.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.ermnvldmr.w.domain.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    @Modifying
    @Query(value = "UPDATE user SET password_sha=SHA1(CONCAT(?4, ?2, ?3)) WHERE id=?1", nativeQuery = true)
    void updatePasswordSha(long id, String login, String password, String salt);

    @Query(value = "SELECT * FROM user WHERE login=?1", nativeQuery = true)
    User findByLogin(String login);

    @Query(value = "SELECT * FROM user WHERE login=?1 AND password_sha=SHA1(CONCAT(?3, ?1, ?2))", nativeQuery = true)
    User findByLoginAndPassword(String login, String password, String salt);

    List<User> findAllByOrderByIdDesc();
}
