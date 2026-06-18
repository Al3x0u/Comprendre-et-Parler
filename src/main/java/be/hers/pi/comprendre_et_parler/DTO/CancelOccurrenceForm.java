package be.hers.pi.comprendre_et_parler.DTO;

public class CancelOccurrenceForm {
    private String date;       // clicked occurrence date, yyyy-MM-dd
    private String scope;
    private String untilDate;  // yyyy-MM-dd, used only for JUSQUA (nullable)

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getUntilDate() { return untilDate; }
    public void setUntilDate(String untilDate) { this.untilDate = untilDate; }
}