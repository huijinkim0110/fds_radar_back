package fds.radar.common;

import lombok.Getter;

@Getter
public enum GoalType {
    EMERGENCY_FUND("비상자금 마련"),
    JEONSE_FUND("전세자금 마련"),
    HOUSE_PURCHASE("주택구매 자금 마련"),
    TRAVEL_FUND("여행자금 마련"),
    WEDDING_FUND("결혼자금 마련"),
    RETIREMENT("노후 준비"),
    SHORT_TERM_SAVING("단기 목돈 마련"),
    LONG_TERM_ASSET_GROWTH("장기 자산 증식"), 
    ETC("기타");

    private final String typeName;
    GoalType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
