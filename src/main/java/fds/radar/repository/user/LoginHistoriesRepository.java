package fds.radar.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.entity.user.LoginHistories;

public interface LoginHistoriesRepository
        extends JpaRepository<LoginHistories, Long> {

    List<LoginHistories> findByUser_UserIdOrderByAttemptedAtDesc(
            Long userId
    );
}