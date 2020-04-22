/**
 * Сущность <platform>/sakura.units
 * (справочник единиц техники)
 *
 * 06.06.2018 by Alex Tues
 * 12.07.2018
 */
package com.tequila.data.domain.platform;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "units")
public class Units implements Serializable {
    private static final long serialVersionUID = 9036275251966901942L;

    // Первичный ключ
    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private Long id;
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    // Поля
    @Column(nullable = false, updatable = false)
    private String name;
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    private String type;
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    @Column(nullable = false, updatable = false)
    private Long imei;
    public void setImei(Long imei) {
        this.imei = imei;
    }
    public Long getImei() {
        return imei;
    }

    private String model;
    public void setModel(String model) {
        this.model = model;
    }
    public String getModel() {
        return model;
    }

    @Column(nullable = false, updatable = false)
    private Timestamp timestamp_edit;
    public void setTimestamp_edit(Timestamp timestamp_edit) {
        this.timestamp_edit = timestamp_edit;
    }
    public Timestamp getTimestamp_edit() {
        return timestamp_edit;
    }

    private String unit_group;
    public void setUnit_group(String unit_group) {
        this.unit_group = unit_group;
    }
    public String getUnit_group() {
        return unit_group;
    }

    private Long unit_type;
    public void setUnit_type(Long unit_type) {
        this.unit_type = unit_type;
    }
    public Long getUnit_type() {
        return unit_type;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("Entity of type %s with id=%d:%n", this.getClass().getName(), getId()))
                .append("name=").append(getName()).append("; ")
                .append("type=").append(getType()).append("; ")
                .append("imei=").append(getImei()).append("; ")
                .append("unit_type=").append(getUnit_type())
                .toString();
    }
}
