package model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Doctor extends User {
    public enum Speciality {
        CARDIOLOGIST,
        ORTHOPEDIST,
        DERMATOLOGIST,
        DENTIST
    }

    private List<Speciality> specialities;
    private int yearsOfExperience;

    public Doctor(UUID id, String name, LocalDate birthday, String mail, Gender gender, List<Speciality> specialities,
            int yearsOfExperience) {
        super(id, name, birthday, mail, gender);
        this.specialities = specialities;
        this.yearsOfExperience = yearsOfExperience;
    }

    public void setSpecialities(List<Speciality> specialities) {
        this.specialities = specialities;
    }

    public List<Speciality> getSpecialities() {
        return specialities;
    }

    public void setExperience(int experience) {
        this.yearsOfExperience = experience;
    }

    public int getExperience() {
        return yearsOfExperience;
    }
}
