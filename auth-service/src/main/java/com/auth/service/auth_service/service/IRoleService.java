package com.auth.service.auth_service.service;


import com.auth.service.auth_service.model.Role;

import java.util.List;

public interface IRoleService {

    public List<Role> findAll();
    public Role findById(Long id);
    public Role save(Role role);
    public void deleteById(Long id);
    public Role updateById(Long id, Role role);
}
