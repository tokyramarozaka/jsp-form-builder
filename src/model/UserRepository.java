package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository {
    private List<User> users;

    public UserRepository() {
        this.users = new ArrayList<>();
    }

    public UserRepository(List<User> users) {
        this.users = users;
    }

    public List<User> findAll() {
        return this.users;
    }

    public void add(User toAdd) {
        this.users.add(toAdd);
    }

    public boolean removeById(UUID id) {
        return this.users.removeIf(user -> user.getId().equals(id));
    }

    public User findById(UUID id) {
        var optionalUser = this.users.stream()
                .findFirst();

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        throw new IllegalArgumentException("User with id: " + id + " has not been found");
    }
}
