package be.hers.pi.comprendre_et_parler.models;

public class Importance {
    private Beneficiary beneficiary;
    private int importance;

    public Importance (Beneficiary beneficiary, int importance){
        this.beneficiary = beneficiary;
        this.importance = importance;
    }

    public Beneficiary getBeneficiary() {
        return this.beneficiary;
    }

    public void setBeneficiary(final Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }

    public int getImportance() {
        return this.importance;
    }

    public void setImportance(final int importance) {
        this.importance = importance;
    }
}
