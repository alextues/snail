/**
 * Структура соответствия ретранслятора, единицы техники и imei-кода
 *
 * Каждому ретранслятору может соответствовать n(>=0) единиц техники;
 * каждая единица техники может соответствовать m(>=) ретрансляторам.
 *
 * 15.06.2018 by Alex Tues
 * 13.07.2018
 */
package com.tequila.common;

import java.io.Serializable;

public class BindingEntity implements Serializable{
    private static final long serialVersionUID = -1563146516143690845L;

    private Long id;
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

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

    private Long imei;
    public void setImei (Long imei) {
        this.imei = imei;
    }
    public Long getImei() {
        return imei;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("Entity of type %s id=%d:%n", this.getClass().getName(), getId()))
                .append("id_retranslator=").append(getId_retranslator()).append("; ")
                .append("id_unit=").append(getId_unit()).append("; ")
                .append("imei=").append(getImei())
                .toString();
    }
}
