package model;

import java.time.LocalDate;
import java.util.UUID;

public class Patient extends User {
    private String disease;
    private int severity;

    public Patient(UUID id, String name, LocalDate birthday, String mail, Gender gender, String disease,
            int severity) {
        super(id, name, birthday, mail, gender);
        this.disease = disease;
        this.severity = severity;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDisease() {
        return disease;
    }

    public void setSeverity(int severity) {
        if (severity >= 1 && severity <= 10) {
            this.severity = severity;
        } else {
            throw new IllegalArgumentException("Severity must be between 1 and 10");
        }
    }

    public int getSeverity() {
        return severity;
    }

}