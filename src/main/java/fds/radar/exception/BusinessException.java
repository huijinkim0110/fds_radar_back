package fds.radar.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final int status;

    public BusinessException(String errorCode, int status, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
