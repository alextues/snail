/**
 * Сущность <platform>/sakura.retranslators
 * (справочник ретрансляторов)
 *
 * 06.06.2018 by Alex Tues
 * 12.07.2018
 */
package com.tequila.data.domain.platform;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "retranslators")
public class Retranslators implements Serializable {
    private static final long serialVersionUID = 2002650720711893034L;

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
    private String name;
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    private String protocol;
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public String getProtocol() {
        return protocol;
    }

    @Column(nullable = false, updatable = false)
    private Timestamp timestamp_edit;
    public void setTimestamp_edit(Timestamp timestamp_edit) {
        this.timestamp_edit = timestamp_edit;
    }
    public Timestamp getTimestamp_edit() {
        return timestamp_edit;
    }

    private Boolean enabled;
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public Boolean getEnabled() {
        return enabled;
    }

    private String filter;
    public void setFilter(String filter) {
        this.filter = filter;
    }
    public String getFilter() {
        return filter;
    }

    private Integer timeout;
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
    public Integer getTimeout() {
        return timeout;
    }

    private Boolean sendflags;
    public void setSendflags(Boolean sendflags) {
        this.sendflags = sendflags;
    }
    public Boolean getSendflags() {
        return sendflags;
    }

    private Boolean sendgeodata;
    public void setSendgeodata(Boolean sendgeodata) {
        this.sendgeodata = sendgeodata;
    }
    public Boolean getSendgeodata() {
        return sendgeodata;
    }

    private Boolean sendin;
    public void setSendin(Boolean sendin) {
        this.sendin = sendin;
    }
    public Boolean getSendin() {
        return sendin;
    }

    private Boolean sendout;
    public void setSendout(Boolean sendout) {
        this.sendout = sendout;
    }
    public Boolean getSendout() {
        return sendout;
    }

    private Integer interval;
    public void setInterval(Integer interval) {
        this.interval = interval;
    }
    public Integer getInterval() {
        return interval;
    }

    private String password;
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    private String request_path;
    public void setRequest_path(String request_path) {
        this.request_path = request_path;
    }
    public String getRequest_path() {
        return request_path;
    }

    private String response_path;
    public void setResponse_path(String response_path) {
        this.response_path = response_path;
    }
    public String getResponse_path() {
        return response_path;
    }

    private String url;
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    private String username;
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
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

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("Entity of type %s with id=%d:%n", this.getClass().getName(), getId()))
                .append("name=").append(getName()).append("; ")
                .append("protocol=").append(getProtocol()).append("; ")
                .append("enabled=").append(getEnabled()).append("; ")
                .append("filter=").append(getFilter()).append("; ")
                .append("host:port=").append(getHost()).append(":").append(getPort())
                .toString();
    }
}
