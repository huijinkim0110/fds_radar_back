package fds.radar.dto.financial;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class FinancialRecordRequest {
    private String category;
    private String type;
    private Long amount;
    private LocalDate recordDate;
}
