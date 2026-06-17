package be.hers.pi.comprendre_et_parler.DTO;

import java.time.LocalDateTime;

public class CreateUnavailability {
    private String reason;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    /**
     * @return this.reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the new reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return this.startDate
     */
    public LocalDateTime getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the new start date
     */
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * @return this.endDate
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the new end date
     */
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}