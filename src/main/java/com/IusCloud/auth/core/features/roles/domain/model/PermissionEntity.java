package com.IusCloud.auth.core.features.roles.domain.model;

import com.IusCloud.auth.core.base.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionEntity extends BaseModel {

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "description", length = 255)
    private String description;
}
