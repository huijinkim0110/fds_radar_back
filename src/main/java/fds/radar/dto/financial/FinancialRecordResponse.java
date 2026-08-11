package fds.radar.dto.financial;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinancialRecordResponse {
    private Long id;
    private String category;
    private String type;
    private Long amount;
    private LocalDate recordDate;
}
