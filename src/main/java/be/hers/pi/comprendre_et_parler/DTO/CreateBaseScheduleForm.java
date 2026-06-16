package be.hers.pi.comprendre_et_parler.DTO;

import java.util.List;

public class CreateBaseScheduleForm {
    private String anchorType;            // "interpreter" or "beneficiary"
    private int anchorId;
    private String startDate;             // ISO yyyy-MM-dd
    private String endDate;
    private List<BaseScheduleEntry> entries;

    public String getAnchorType() { return anchorType; }

    public void setAnchorType(String anchorType) { this.anchorType = anchorType; }

    public int getAnchorId() { return anchorId; }

    public void setAnchorId(int anchorId) { this.anchorId = anchorId; }

    public String getStartDate() { return startDate; }

    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }

    public void setEndDate(String endDate) { this.endDate = endDate; }

    public List<BaseScheduleEntry> getEntries() { return entries; }

    public void setEntries(List<BaseScheduleEntry> entries) { this.entries = entries; }
}