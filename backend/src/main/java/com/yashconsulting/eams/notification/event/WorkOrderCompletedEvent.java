package com.yashconsulting.eams.notification.event;

import lombok.Value;

@Value
public class WorkOrderCompletedEvent {
    Long workOrderId;
    String workOrderNumber;
    String assignedTechnician;
}
