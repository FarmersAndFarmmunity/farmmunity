package com.shop.farmmunity.base.baseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor
@SuperBuilder
@EntityListeners(value = {AuditingEntityListener.class})
@MappedSuperclass
@Getter
public class BaseEntity {
    @CreatedBy
    @Column(updatable = false)
    private String createdBy; // 생성한 사람

    @LastModifiedBy
    private String modifiedBy; // 마지막으로 수정한 사람
}
