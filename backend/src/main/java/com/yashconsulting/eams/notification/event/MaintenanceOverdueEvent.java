package com.yashconsulting.eams.notification.event;

import lombok.Value;

@Value
public class MaintenanceOverdueEvent {
    Long planId;
    String planCode;
    String planName;
}
