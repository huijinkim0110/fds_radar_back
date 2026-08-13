package fds.radar.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // 우리가 정의한 커스텀 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse body = ErrorResponse.builder()
                                          .errorCode(e.getErrorCode())
                                          .message(e.getMessage())
                                          .build();

        return ResponseEntity.status(e.getStatus()).body(body);
    }

    // 지금까지 써온 IllegalStateException -> 400 Bad Request로 변환
    // (투자성향 진단 없음, 이미 등록된 관심상품 등)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        ErrorResponse body = ErrorResponse.builder()
                                          .errorCode("INVALID_STATE")
                                          .message(e.getMessage())
                                          .build();

        return ResponseEntity.badRequest().body(body);
    }

    // 그 외 예상 못한 모든 예외 -> 500, 상세 내용은 숨기고 일반 메세지만
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        ErrorResponse body = ErrorResponse.builder()
                                          .errorCode("INTERNAL_ERROR")
                                          .message("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                                          .build();

        return ResponseEntity.internalServerError().body(body);
    }
}
