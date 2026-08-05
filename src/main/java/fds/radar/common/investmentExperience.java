package fds.radar.common;

public enum investmentExperience {
    NONE("투자 경험 없음"),
    BEGINNER("예/적금 등 안전자산 위주 경험"),
    INTERMEDIATE("펀드, 채권 등 경험 있음"),
    EXPERIENCED("주식, 파생상품 등 고위험 상품 경험 있음");

    private final String statusName;

    investmentExperience(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
