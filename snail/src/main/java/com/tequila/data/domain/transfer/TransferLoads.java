/**
 * Сущность <transfer>/delivery.t_loads
 * (таблица загрузок сообщений из таблицы <platform>/sakura.messages)
 *
 * 20.06.2018 by Alex Tues
 * 26.06.2018
 * 12.07.2018
 * 18.07.2018
 */
package com.tequila.data.domain.transfer;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "t_loads")
public class TransferLoads implements Serializable {
    private static final long serialVersionUID = -6769630621327282132L;

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
    private Long fid;
    public void setFid(Long fid) {
        this.fid = fid;
    }
    public Long getFid() {
        return fid;
    }

    @Column(nullable = false, updatable = false)
    private Long tid;
    public void setTid(Long tid) {
        this.tid = tid;
    }
    public Long getTid() {
        return tid;
    }

    @Column(nullable = false, updatable = false)
    private Long count;
    public void setCount(Long count) {
        this.count = count;
    }
    public Long getCount() {
        return count;
    }

    private Timestamp loaded;
    public void setLoaded(Timestamp loaded) {
        this.loaded = loaded;
    }
    public Timestamp getLoaded() {
        return loaded;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("Entity of type %s with id=%d:%n", this.getClass().getName(), getId()))
                .append("fid=").append(getFid()).append("; ")
                .append("tid=").append(getTid()).append("; ")
                .append("count=").append(getCount()).append("; ")
                .append("loaded=").append(getLoaded())
                .toString();
    }
}
