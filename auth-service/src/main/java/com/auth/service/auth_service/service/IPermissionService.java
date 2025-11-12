package com.auth.service.auth_service.service;


import com.auth.service.auth_service.model.Permission;

import java.util.List;

public interface IPermissionService {

    public List<Permission> findAll();
    public Permission findBYId(Long id);
    public Permission save(Permission permission);
    public void deleteById(Long id);
    public Permission updateById(Long id, Permission permission);
}

