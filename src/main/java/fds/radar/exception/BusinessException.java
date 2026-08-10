package fds.radar.exception;

public class BusinessException extends RuntimeException{
    
    // 400 - 비즈니스 규칙 위반 (잔액이 있는데 해지 등)
    public BusinessException(String message) {super(message);}
}
