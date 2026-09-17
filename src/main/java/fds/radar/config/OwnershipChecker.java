package fds.radar.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import fds.radar.exception.BusinessException;

@Component 
public class OwnershipChecker {
    
    // 경로(PathVariable)의 userId가 토큰 주인과 같은지 확인. ADMIN은 예외로 통과
    public void verify(Long pathUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                              .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        Long tokenUserId = (Long) auth.getPrincipal();

        if (!tokenUserId.equals(pathUserId)) {
            throw new BusinessException("ACCESS_DENIED", 403, "본인 정보만 접근할 수 있습니다.");
        }
    }
}
