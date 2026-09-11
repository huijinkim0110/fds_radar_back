package fds.radar.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fds.radar.entity.user.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByPhone(String phone); // 전화번호로 회원 조회

    Optional<Users> findByEmail(String Email); // 로그인시 이메일로 유저 조회
    boolean existsByEmail(String email); // 회원가입 시 중복 이메일 체크

    // 상담원 자동 배정 - 현재 IN_PROGRESS 상담 건수가 가장 적은 ADMIN 한 명 조회
    @Query(value="SELECT u.* FROM users u " +
                 "LEFT JOIN chat_sessions cs ON cs.assigned_admin_id = u.user_id AND cs.status = 'IN_PROGRESS' " +
                 "WHERE u.role = 'ADMIN' " +
                 "GROUP BY u.user_id " + 
                 "ORDER BY COUNT(cs.session_id) ASC " +
                 "LIMIT 1", nativeQuery = true)
    Optional<Users> findLeastBusyAdmin();
 
}
