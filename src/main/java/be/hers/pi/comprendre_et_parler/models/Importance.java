package be.hers.pi.comprendre_et_parler.models;

public class Importance {
    private Beneficiary beneficiary;
    private int importance;

    /**
     * Constructor of an Importance object
     * @param beneficiary represent the beneficiary linked to the mission
     * @param importance represent the importance level of the beneficiary in the mission
     */
    public Importance(Beneficiary beneficiary, int importance) {
        this.beneficiary = beneficiary;
        this.importance = importance;
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
     * @return this.importance
     */
    public int getImportance() {
        return importance;
    }

    /**
     * @param importance represent the new importance level
     */
    public void setImportance(int importance) {
        this.importance = importance;
    }

    /**
     * Compare this Importance with another Importance for equality
     * @param other the Importance object to compare with
     * @return true if both Importance objects have identical beneficiary and importance
     */
    public boolean equals(Importance other) {
        return (beneficiary == other.beneficiary && importance == other.importance);
    }

    /**
     * Return a String representation of the Importance containing all fields
     * @return formatted string with beneficiary and importance
     */
    @Override
    public String toString() {
        return null;
    }
}
