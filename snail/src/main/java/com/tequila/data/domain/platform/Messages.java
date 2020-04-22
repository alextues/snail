/**
 * Сущность <platform>/sakura.messages
 * (полученные телематической платформой сообщения)
 *
 * 06.06.2018 by Alex Tues
 * 12.07.2018
 */
package com.tequila.data.domain.platform;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "messages")
public class Messages implements Serializable {
    private static final long serialVersionUID = 145138685104577515L;

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
    private Date timestamp_create;
    public void setTimestamp_create(Date timestamp_create) {
        this.timestamp_create = timestamp_create;
    }
    public Date getTimestamp_create() {
        return timestamp_create;
    }

    private Double lat;
    public void setLat(Double lat) {
        this.lat = lat;
    }
    public Double getLat() {
        return lat;
    }

    private Double lon;
    public void setLon(Double lon) {
        this.lon = lon;
    }
    public Double getLon() {
        return lon;
    }

    private String params;
    public void setParams(String params) {
        this.params = params;
    }
    public String getParams() {
        return params;
    }

    private Double speed;
    public void setSpeed(Double speed) {
        this.speed = speed;
    }
    public Double getSpeed() {
        return speed;
    }

    private Long imei;
    public void setImei(Long imei) {
        this.imei = imei;
    }
    public Long getImei() {
        return imei;
    }

    private Double hdop;
    public void setHdop(Double hdop) {
        this.hdop = hdop;
    }
    public Double getHdop() {
        return hdop;
    }

    private Integer input;
    public void setInput(Integer input) {
        this.input = input;
    }
    public Integer getInput() {
        return input;
    }

    private Integer output;
    public void setOutput(Integer output) {
        this.output = output;
    }
    public Integer getOutput() {
        return output;
    }

    private Integer satellites_count;
    public void setSatellites_count(Integer satellites_count) {
        this.satellites_count = satellites_count;
    }
    public Integer getSatellites_count() {
        return satellites_count;
    }

    private Double altitude;
    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }
    public Double getAltitude() {
        return altitude;
    }

    private Long flags;
    public void setFlags(Long flags) {
        this.flags = flags;
    }
    public Long getFlags() {
        return flags;
    }

    private Integer course;
    public void setCourse(Integer course) {
        this.course = course;
    }
    public Integer getCourse() {
        return course;
    }

    private Timestamp timestamp_edit;
    public void setTimestamp_edit(Timestamp timestamp_edit) {
        this.timestamp_edit = timestamp_edit;
    }
    public Timestamp getTimestamp_edit() {
        return timestamp_edit;
    }

    private Long unit;
    public void setUnit(Long unit) {
        this.unit = unit;
    }
    public Long getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("Entity of type %s with id=%d:%n", this.getClass().getName(), getId()))
                .append("lat=").append(getLat()).append("; ")
                .append("lon=").append(getLon()).append("; ")
                .append("timestamp_create=").append(getTimestamp_create()).append("; ")
                .append("imei=").append(getImei())
                .toString();
    }
}
