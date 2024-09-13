package com.vega.praksa.service;

import com.vega.praksa.model.Role;

import java.util.List;

public interface RoleService {

    List<Role> findByName(String name);

}
