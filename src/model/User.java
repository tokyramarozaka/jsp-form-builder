package model;

import java.time.LocalDate;
import java.util.UUID;

public abstract class User {
    public enum Gender {
        M, F, OTHER
    }

    private UUID id;
    private String name;
    private LocalDate birthday;
    private String mail;
    private Gender gender;

    public User(UUID id, String name, LocalDate birthday, String mail, User.Gender gender) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.mail = mail;
        this.gender = gender;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User(id=" + this.id + ", name=" + this.name + ")";
    }
}
