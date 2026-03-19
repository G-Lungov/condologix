package com.condologix.application.resident;

import com.condologix.application.unit.UnitModel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "residents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResidentModel {

    private long id;
    private UnitModel unit;
    private String name;
    private String email;
    private long phone;


}
