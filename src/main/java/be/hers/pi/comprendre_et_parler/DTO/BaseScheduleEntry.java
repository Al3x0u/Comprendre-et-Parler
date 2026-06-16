package be.hers.pi.comprendre_et_parler.DTO;

public class BaseScheduleEntry {
    private int dayOfWeek;                 // 1-7 (Mon-Sun, ISO)
    private String startTime;             // HH:mm
    private String endTime;
    private int otherActorId;             // beneficiary id if anchor is interpreter, else interpreter id
    private String title;
    private int locationId;
    private Integer jobSkillId;           // nullable
    private Integer academicSkillId;      // nullable
    private String room;                  // nullable

    public int getDayOfWeek() { return dayOfWeek; }

    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getStartTime() { return startTime; }

    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }

    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getOtherActorId() { return otherActorId; }

    public void setOtherActorId(int otherActorId) { this.otherActorId = otherActorId; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public int getLocationId() { return locationId; }

    public void setLocationId(int locationId) { this.locationId = locationId; }

    public Integer getJobSkillId() { return jobSkillId; }

    public void setJobSkillId(Integer jobSkillId) { this.jobSkillId = jobSkillId; }

    public Integer getAcademicSkillId() { return academicSkillId; }

    public void setAcademicSkillId(Integer academicSkillId) { this.academicSkillId = academicSkillId; }

    public String getRoom() { return room; }

    public void setRoom(String room) { this.room = room; }
}