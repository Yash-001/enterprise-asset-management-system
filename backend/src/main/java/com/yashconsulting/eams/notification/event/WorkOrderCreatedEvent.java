package com.yashconsulting.eams.notification.event;

import lombok.Value;

@Value
public class WorkOrderCreatedEvent {
    Long workOrderId;
    String workOrderNumber;
    String assignedTechnician;
}
