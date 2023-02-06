package com.ubi.masterservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class Auditable {

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date created;

    @LastModifiedBy
    @Column(nullable = false)
    private Long modifiedBy;

    @LastModifiedDate
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date modified;

    @Column(nullable = false)
    private Boolean isDeleted = false;
}
