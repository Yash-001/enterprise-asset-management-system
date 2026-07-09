package com.yashconsulting.eams.notification.event;

import lombok.Value;

@Value
public class LowStockEvent {
    Long sparePartId;
    String partNumber;
    String partName;
    int currentStock;
    int minimumStock;
}
