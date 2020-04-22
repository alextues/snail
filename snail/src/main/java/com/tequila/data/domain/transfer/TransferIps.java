/**
 * Сущность <transfer>/delivery.t_ips
 * (таблица сообщений для ретрансляции по протоколу Wialon IPS)
 *
 * 27.06.2018 by Alex Tues
 * 12.07.2018
 * 18.07.2018
 */
package com.tequila.data.domain.transfer;

import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name="t_ips")
@SelectBeforeUpdate(value = false)
public class TransferIps implements Serializable {
    private static final long serialVersionUID = -5402193725452855631L;

    // Первичный ключ
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Long id_message;
    public void setId_message(Long id_message) {
        this.id_message = id_message;
    }
    public Long getId_message() {
        return id_message;
    }

    @Column(nullable = false, updatable = false)
    private Long id_retranslator;
    public void setId_retranslator(Long id_retranslator) {
        this.id_retranslator = id_retranslator;
    }
    public Long getId_retranslator() {
        return id_retranslator;
    }

    @Column(nullable = false, updatable = false)
    private Long imei;
    public void setImei(Long imei) {
        this.imei = imei;
    }
    public Long getImei() {
        return imei;
    }

    private Integer port;
    public void setPort(Integer port) {
        this.port = port;
    }
    public Integer getPort() {
        return port;
    }

    private String host;
    public void setHost(String host) {
        this.host = host;
    }
    public String getHost() {
        return host;
    }

    private String lpackage;
    public void setLpackage(String lpackage) {
        this.lpackage = lpackage;
    }
    public String getLpackage() {
        return lpackage;
    }

    private String dpackage;
    public void setDpackage(String dpackage) {
        this.dpackage = dpackage;
    }
    public String getDpackage() {
        return dpackage;
    }

    @Column(nullable = false, updatable = false)
    private Timestamp timestamp_create;
    public void setTimestamp_create(Timestamp timestamp_create) {
        this.timestamp_create = timestamp_create;
    }
    public Timestamp getTimestamp_create() {
        return timestamp_create;
    }

    @Column(updatable = true)
    private Timestamp timestamp_send;
    public void setTimestamp_send(Timestamp timestamp_send) {
        this.timestamp_send = timestamp_send;
    }
    public Timestamp getTimestamp_send() {
        return timestamp_send;
    }

    @Column(updatable = false)
    private Timestamp timestamp_stop;
    public void setTimestamp_stop(Timestamp timestamp_stop) {
        this.timestamp_stop = timestamp_stop;
    }
    public Timestamp getTimestamp_stop() {
        return timestamp_stop;
    }

    private Integer attempt;
    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }
    public Integer getAttempt() {
        return attempt;
    }

    private Integer attempt_limit;
    public void setAttempt_limit(Integer attempt_limit) {
        this.attempt_limit = attempt_limit;
    }
    public Integer getAttempt_limit() {
        return attempt_limit;
    }

    private Boolean delivered;
    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }
    public Boolean getDelivered() {
        return delivered;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("Entity of type %s:%n", this.getClass().getName()))
                .append(String.format("id              = %d%n", getId()))
                .append(String.format("id_message      = %d%n", getId_message()))
                .append(String.format("id_retranslator = %d%n", getId_retranslator()))
                .append(String.format("host:port       = %s%n", getHost() + ":" + getPort()))
                .append(String.format("lpackage        = %s%n", getLpackage()))
                .append(String.format("dpackage        = %s%n", getDpackage()))
                .append(String.format("created         = %s%n", getTimestamp_create()))
                .append(String.format("delivered       = %s%n", getDelivered()))
                .toString();
    }
}
