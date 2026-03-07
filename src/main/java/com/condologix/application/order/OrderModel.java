package com.condologix.application.order;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderModel {

    private long id;
    private String senderInfo;
    private LocalDateTime arrivalDate;
    private boolean scan;

}
