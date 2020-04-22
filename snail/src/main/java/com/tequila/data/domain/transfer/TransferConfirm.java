/**
 * Сущность <transfer>/delivery.t_confirm
 * (таблица признаков ожидания подтверждения при отправке данных)
 *
 * 07.08.2018 by Alex Tues
 */
package com.tequila.data.domain.transfer;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="t_confirm")
public class TransferConfirm implements Serializable {
    private static final long serialVersionUID = 155124601813666789L;

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
    private Long id_retranslator;
    public void setId_retranslator(Long id_retranslator) {
        this.id_retranslator = id_retranslator;
    }
    public Long getId_retranslator() {
        return id_retranslator;
    }

    private Boolean confirm;
    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
    }
    public Boolean getConfirm() {
        return confirm;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("Entity of type %s:%n", this.getClass().getName()))
                .append(String.format("id              = %d%n", getId()))
                .append(String.format("id_retranslator = %d%n", getId_retranslator()))
                .append(String.format("confirm         = %s%n", getConfirm()))
                .toString();
    }
}
