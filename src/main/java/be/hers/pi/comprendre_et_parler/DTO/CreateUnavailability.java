package be.hers.pi.comprendre_et_parler.DTO;

import java.time.LocalDateTime;

public class CreateUnavailability {
    private String reason;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
