package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A class that contains a list of users, with the useful methods it needs.
 * Will be instantiated in my JSP: using <jsp:useBean>
 */
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

    public void crupdate(User toCrupdate) {
        System.out.println("Crupdate is on");
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(toCrupdate.getId())) {
                users.set(i, toCrupdate);
                return;
            }
        }
        users.add(toCrupdate);
    }

    public void add(User toAdd) {
        this.users.add(toAdd);
    }

    public boolean deleteById(UUID id) {
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
