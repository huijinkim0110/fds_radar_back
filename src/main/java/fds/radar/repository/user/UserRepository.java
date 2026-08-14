package fds.radar.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.user.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);
}