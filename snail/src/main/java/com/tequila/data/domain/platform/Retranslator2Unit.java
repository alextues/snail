/**
 * Сущность <platform>/sakura.retranslator2unit
 * (привязка ретрансляторов к единицам техники)
 *
 * 09.06.2018 by Alex Tues
 * 12.07.2018
 */
package com.tequila.data.domain.platform;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "retranslator2unit")
public class Retranslator2Unit implements Serializable {
    private static final long serialVersionUID = 5507618399162272177L;

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
    private Long id_retranslator;
    public void setId_retranslator(Long id_retranslator) {
        this.id_retranslator = id_retranslator;
    }
    public Long getId_retranslator() {
        return id_retranslator;
    }

    private Long id_unit;
    public void setId_unit(Long id_unit) {
        this.id_unit = id_unit;
    }
    public Long getId_unit() {
        return id_unit;
    }

    @Column(nullable = false, updatable = false)
    private Timestamp timestamp_edit;
    public void setTimestamp_edit(Timestamp timestamp_edit) {
        this.timestamp_edit = timestamp_edit;
    }
    public Timestamp getTimestamp_edit() {
        return timestamp_edit;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("Entity of type %s with id=%d:%n", this.getClass().getName(), getId()))
                .append("id_retranslator=").append(getId_retranslator()).append("; ")
                .append("id_unit=").append(getId_unit())
                .toString();
    }
}
