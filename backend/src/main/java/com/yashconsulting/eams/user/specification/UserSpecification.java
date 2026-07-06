package com.yashconsulting.eams.user.specification;

import com.yashconsulting.eams.user.dto.UserSearchRequest;
import com.yashconsulting.eams.user.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class UserSpecification {

    private UserSpecification() {
        // Utility class constructor private instantiation restriction
    }

    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("firstName")), "%" + firstName.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<User> hasLastName(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null || lastName.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("lastName")), "%" + lastName.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("email")), "%" + email.trim().toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<User> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<User> build(UserSearchRequest request) {
        if (request == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return Specification.allOf(
                hasFirstName(request.getFirstName()),
                hasLastName(request.getLastName()),
                hasEmail(request.getEmail()),
                isActive(request.getActive())
        );
    }
}
