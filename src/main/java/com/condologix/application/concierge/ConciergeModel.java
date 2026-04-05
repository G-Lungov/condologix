package com.condologix.application.concierge;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concierges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConciergeModel {

    private long id;
    private String name;
    private long phone;

}
