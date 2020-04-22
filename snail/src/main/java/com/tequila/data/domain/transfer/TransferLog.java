/**
 * Сущность <transfer>/delivery.t_log
 * (таблица регистрации событий по ретрансляции сообщений)
 *
 * 13.07.2018 by Alex Tues
 * 18.07.2018
 */
package com.tequila.data.domain.transfer;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "t_log")
public class TransferLog implements Serializable {
    private static final long serialVersionUID = 6415111198006306604L;

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
    private Long id_parent;
    public void setId_parent(Long id_parent) {
        this.id_parent = id_parent;
    }
    public Long getId_parent() {
        return id_parent;
    }

    @Column(nullable = false, updatable = false)
    private Long id_message;
    public void setId_message(Long id_message) {
        this.id_message = id_message;
    }
    public Long getId_message() {
        return id_message;
    }

    @Column(nullable = false, updatable = false)
    private String protocol;
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public String getProtocol() {
        return protocol;
    }

    @Column(nullable = false, updatable = false)
    private Timestamp timestamp_create;
    public void setTimestamp_create(Timestamp timestamp_create) {
        this.timestamp_create = timestamp_create;
    }
    public Timestamp getTimestamp_create() {
        return timestamp_create;
    }

    private Boolean delivered;
    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }
    public Boolean getDelivered() {
        return delivered;
    }

    private String comment;
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("Entity of type %s with id=%d:%n", this.getClass().getName(), getId()))
                .append("id_message=").append(getId_message()).append("; ")
                .append("protocol=").append(getProtocol()).append("; ")
                .append("timestamp_create=").append(getTimestamp_create()).append("; ")
                .append("delivered=").append(getDelivered()).append("; ")
                .append("comment=").append(getComment())
                .toString();

    }
}
