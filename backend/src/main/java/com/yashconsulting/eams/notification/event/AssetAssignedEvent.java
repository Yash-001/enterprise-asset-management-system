package com.yashconsulting.eams.notification.event;

import lombok.Value;

@Value
public class AssetAssignedEvent {
    Long assetId;
    String assetCode;
    Long employeeId;
}
