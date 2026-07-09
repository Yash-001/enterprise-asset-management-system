package com.yashconsulting.eams.notification.specification;

import com.yashconsulting.eams.notification.dto.NotificationSearchRequest;
import com.yashconsulting.eams.notification.entity.Notification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NotificationSpecification {

    public static Specification<Notification> getSpecification(NotificationSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Exclude soft-deleted records
            predicates.add(cb.equal(root.get("active"), true));

            if (request.getRecipientUserId() != null) {
                predicates.add(cb.equal(root.get("recipientUserId"), request.getRecipientUserId()));
            }

            if (request.getRead() != null) {
                predicates.add(cb.equal(root.get("read"), request.getRead()));
            }

            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + request.getTitle().toLowerCase().trim() + "%"));
            }

            if (request.getNotificationType() != null) {
                predicates.add(cb.equal(root.get("notificationType"), request.getNotificationType()));
            }

            if (request.getPriority() != null) {
                predicates.add(cb.equal(root.get("priority"), request.getPriority()));
            }

            if (StringUtils.hasText(request.getReferenceType())) {
                predicates.add(cb.equal(root.get("referenceType"), request.getReferenceType().trim()));
            }

            if (request.getReferenceId() != null) {
                predicates.add(cb.equal(root.get("referenceId"), request.getReferenceId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
