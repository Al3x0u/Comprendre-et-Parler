    package be.hers.pi.comprendre_et_parler.models;

    import java.util.*;
    import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

    public class Mission {
        private int id = -1;
        private String subject;
        private String commentary;
        private Set<TimeSlot> timeSlot;
        private Beneficiary beneficiary;
        private Set<Interpreter> interpreters;
        private Location location;
        private JobSkill jobSkill;
        private AcademicSkill academicSkill;
        private String room;
        private int importance = 0;

        /**
         * Constructor of a Mission without beneficiary
         * @param id represent the id
         * @param subject represent the subject
         * @param commentary represent the commentary
         * @param timeSlot represent the time
         * @param location represent the location
         * @param interpreters represent the interpreters
         * @param room represent the room
         * @param importance represent the importance
         */
        public Mission(int id, String subject, String commentary, Set<TimeSlot> timeSlot, Location location,
                       HashSet<Interpreter> interpreters, String room, int importance) {
            if (id > 0) this.id = id;
            this.subject = subject;
            this.commentary = commentary;
            this.timeSlot = timeSlot;
            this.beneficiary = null;
            this.interpreters = interpreters;
            this.location = location;
            this.jobSkill = null;
            this.academicSkill = null;
            this.room = room;
            if (importance >= 0 && importance <= 3) this.importance = importance;
        }

        /**
         * Constructor of a Mission without id and without beneficiary
         * @param subject represent the subject
         * @param commentary represent the commentary
         * @param timeSlot represent the time slot
         * @param location represent the location
         * @param interpreters represent the interpreters
         * @param room represent the room
         * @param importance represent the importance
         */
        public Mission(String subject, String commentary, Set<TimeSlot> timeSlot, Location location,
                       HashSet<Interpreter> interpreters, String room, int importance) {
            this(-1, subject, commentary, timeSlot, location, interpreters, room, importance);
        }

        /**
         * Constructor of a Mission without interpreters
         * @param id represent the id
         * @param subject represent the subject
         * @param commentary represent the commentary
         * @param timeSlot represent the time slot
         * @param beneficiary represent the beneficiary
         * @param location represent the location
         * @param jobSkill represent the job skill
         * @param academicSkill represent the academic skill
         * @param room represent the room
         */
        public Mission(int id, String subject, String commentary, Set<TimeSlot> timeSlot,
                       Beneficiary beneficiary, Location location, JobSkill jobSkill,
                       AcademicSkill academicSkill, String room, int importance) {
            if (id > 0) this.id = id;
            this.subject = subject;
            this.commentary = commentary;
            this.timeSlot = timeSlot;
            this.beneficiary = beneficiary;
            this.interpreters = null;
            this.location = location;
            this.jobSkill = jobSkill;
            this.academicSkill = academicSkill;
            this.room = room;
            if (importance >= 0 && importance <= 3) this.importance = importance;
        }

        /**
         * Constructor of a Mission without id and without interpreters
         * @param subject represent the subject
         * @param commentary represent the commentary
         * @param timeSlot represent the time slot
         * @param beneficiary represent the beneficiary
         * @param location represent the location
         * @param jobSkill represent the job skill
         * @param academicSkill represent the academic skill
         * @param room represent the room
         */
        public Mission(String subject, String commentary, Set<TimeSlot> timeSlot,
                       Beneficiary beneficiary, Location location, JobSkill jobSkill,
                       AcademicSkill academicSkill, String room, int importance) {
            this(-1, subject, commentary, timeSlot, beneficiary,
                    location,jobSkill, academicSkill, room, importance);
        }

        /**
         * Copy constructor of a Mission
         * @param other the Mission object to copy, must not be null
         */
        public Mission(Mission other) {
            this.id = other.id;
            this.subject = other.subject;
            this.commentary = other.commentary;
            this.timeSlot = new HashSet<>(other.timeSlot);
            if (other.beneficiary != null)
                this.beneficiary = new Beneficiary(other.beneficiary);

            if (other.interpreters != null)
                this.interpreters = new HashSet<>(other.interpreters);

            this.location = new Location(other.location);

            if (other.jobSkill != null)
                this.jobSkill = new JobSkill(other.jobSkill);

            if (other.academicSkill != null)
                this.academicSkill = new AcademicSkill(other.academicSkill);

            this.room = other.room;
            this.importance = other.importance;
        }

        /**
         * @return this.id
         */
        public int getId() {
            return id;
        }

        /**
         * @param id represent the new id
         */
        public void setId(int id) {
            if (id >= 0) this.id = id;
        }

        /**
         * @return this.subject
         */
        public String getSubject() {
            return subject;
        }

        /**
         * @param subject represent the new subject
         */
        public void setSubject(String subject){
            this.subject = subject;
        }

        /**
         * @param state represent the new mission state
         */
        public void setStateOfMission(MissionState state){
            this.stateOfMission = state;
        }

        /**
         * @return this.commentary
         */
        public String getCommentary() {
            return commentary;
        }

        /**
         * @param commentary represent the mission new commentary
         */
        public void setCommentary(String commentary){
            this.commentary = commentary;
        }

        /**
         * @return this.timeSlot
         */
        public Set<TimeSlot> getTimeSlot() {
            return timeSlot;
        }

        /**
         * @param timeSlot represent the new time slot
         */
        public void setTimeSlot(Set<TimeSlot> timeSlot){
            this.timeSlot = timeSlot;
        }

        /**
         * @return this.beneficiary
         */
        public Beneficiary getBeneficiary() {
            return beneficiary;
        }

        /**
         * @param beneficiary represent the new beneficiary
         */
        public void setBeneficiary(Beneficiary beneficiary) {
            this.beneficiary = beneficiary;
        }

        /**
         * @return this.interpreters
         */
        public Set<Interpreter> getInterpreters() {
            return interpreters;
        }

        /**
         * @param interpreters represent the new interpreters
         * @throws AlreadyExistsException if two interpreters have the same id or are equal
         */
        public void setInterpreters(Set<Interpreter> interpreters) throws AlreadyExistsException {
            List<Interpreter> list = new ArrayList<>(interpreters);
            for (int i = 0; i < list.size(); i++)
                for (int j = i + 1; j < list.size(); j++)
                    if (list.get(i).getId() == list.get(j).getId())
                        throw new AlreadyExistsException("Two interpreters have the same id");
            this.interpreters = interpreters;
        }

        /**
         * @return this.location
         */
        public Location getLocation() {
            return location;
        }

        /**
         * @param location represent the new location
         */
        public void setLocation(Location location) {
            this.location = location;
        }

        /**
         * @return this.jobSkill
         */
        public JobSkill getJobSkill() {
            return jobSkill;
        }

        /**
         * @param jobSkill represent the new job skill
         */
        public void setJobSkill(JobSkill jobSkill) {
            this.jobSkill = jobSkill;
        }

        /**
         * @return this.academicSkill
         */
        public AcademicSkill getAcademicSkill() {
            return academicSkill;
        }

        /**
         * @param academicSkill represent the new academic skill
         */
        public void setAcademicSkill(AcademicSkill academicSkill) {
            this.academicSkill = academicSkill;
        }

        /**
         * @return this.room
         */
        public String getRoom() {
            return room;
        }

        /**
         * @param room represent the new room
         */
        public void setRoom(String room) {
            this.room = room;
        }

        /**
         * @return this.importance
         */
        public int getImportance() {
            return importance;
        }

        /**
         * @param importance represent the new importance
         */
        public void setImportance(int importance) {
            if (importance >= 0 && importance <= 3) this.importance = importance;
        }

        /**
         * Add an Interpreter to the interpreters Set
         * @param interpreter represent the Interpreter to add
         * @throws AlreadyExistsException if an interpreter with the same id already exists in the set
         * @throws NullPointerException if interpreter is null
         */
        public void addInterpreter(Interpreter interpreter) throws AlreadyExistsException, NullPointerException {
            if (interpreter == null)
                throw new NullPointerException("Interpreter cannot be null");

            if (interpreters == null)
                interpreters = new HashSet<>();

            for (Interpreter i : interpreters)
                if (i.getId() == interpreter.getId())
                    throw new AlreadyExistsException("Interpreter already exists in this mission");

            interpreters.add(interpreter);
        }

        /**
         * Remove an Interpreter from the interpreters Set by id
         * @param id represent the id of the Interpreter to remove
         * @throws NoSuchElementException if no interpreter with the given id exists in the set
         */
        public void deleteInterpreter(int id) throws NoSuchElementException {
            if (interpreters == null)
                return;

            Interpreter toRemove = null;
            boolean found = false;
            Iterator<Interpreter> it = interpreters.iterator();
            while (!found && it.hasNext()) {
                Interpreter i = it.next();
                if (i.getId() == id) {
                    toRemove = i;
                    found = true;
                }
            }
            if (!found) throw new NoSuchElementException("No interpreter with id: " + id);
            interpreters.remove(toRemove);
        }

        /**
         * Compare this Mission with another Object for equality
         * @param o the Object to compare with
         * @return true if both objects have identical subject,
         * commentary, timeSlot, beneficiary, interpreters,
         * location, jobSkill, academicSkill, room and importance
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Mission)) return false;

            Mission other = (Mission) o;
            return Objects.equals(subject, other.subject) && Objects.equals(commentary, other.commentary)
                    && Objects.equals(timeSlot, other.timeSlot) && Objects.equals(beneficiary, other.beneficiary)
                    && Objects.equals(interpreters, other.interpreters) && Objects.equals(location, other.location)
                    && Objects.equals(jobSkill, other.jobSkill) && Objects.equals(academicSkill, other.academicSkill)
                    &&  Objects.equals(room, other.room) &&  Objects.equals(importance, other.importance);
        }

        /**
         * Computes the hash code of this Mission
         * two Mission objects that are equal according to equals() will have the same hash code
         * @return an integer hash code representing this Mission (id is not taken into account)
         */
        @Override public int hashCode() {
            return Objects.hash(subject, commentary, timeSlot, beneficiary,
                    interpreters, location, jobSkill, academicSkill, room, importance
            );
        }

        /**
         * Return a String representation of the Mission containing all fields
         * @return formatted string with id, subjet, stateOfMission, commentary, timeSlot, beneficiary, interpreters,
         * location, jobSkill, academicSkill, room and importance
         */
        @Override
        public String toString(){
            return "Mission{id = " + id + ", subject = " + subject + ", commentary = " + commentary
                    + ", timeSlot = " + timeSlot + ", beneficiary = " + beneficiary
                    + ", interpreters = " + interpreters + ", location = " + location + ", jobSkill = " + jobSkill
                    + ", academicSkill = " + academicSkill + ", room = " + room + ", importance = " + importance + "}";
        }
    }