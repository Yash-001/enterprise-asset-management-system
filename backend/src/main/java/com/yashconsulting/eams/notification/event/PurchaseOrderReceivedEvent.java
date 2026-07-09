package com.yashconsulting.eams.notification.event;

import lombok.Value;

@Value
public class PurchaseOrderReceivedEvent {
    Long poId;
    String poNumber;
    String createdBy;
}
