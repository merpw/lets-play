package pw.mer.letsplay.model;

public enum ERole {
    ADMIN,
    USER;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
