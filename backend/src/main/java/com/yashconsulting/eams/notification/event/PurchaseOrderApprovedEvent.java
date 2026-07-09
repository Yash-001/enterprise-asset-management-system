package com.yashconsulting.eams.notification.event;

import lombok.Value;

@Value
public class PurchaseOrderApprovedEvent {
    Long poId;
    String poNumber;
    String createdBy;
}
