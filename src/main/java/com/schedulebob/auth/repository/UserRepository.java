package com.schedulebob.auth.repository;

import com.schedulebob.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * 사용자 정보를 DB에서 접근하기 위한 JPA 리포지토리 인터페이스.
 */
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * 이메일(로그인 ID)로 사용자 조회
   *
   * @param email 로그인에 사용할 이메일 아이디
   * @return Optional<User> 해당 사용자가 존재하면 User 객체 반환, 아니면 빈 Optional
   */
  Optional<User> findByEmail(String email);
}