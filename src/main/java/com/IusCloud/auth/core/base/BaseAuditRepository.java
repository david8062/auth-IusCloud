package com.IusCloud.auth.core.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseAuditRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    @Override
    List<T> findAll();

    @Override
    long count();
}
