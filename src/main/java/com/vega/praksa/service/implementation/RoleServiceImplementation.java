package com.vega.praksa.service.implementation;

import com.vega.praksa.model.Role;
import com.vega.praksa.repository.RoleRepository;
import com.vega.praksa.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImplementation implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImplementation(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> findByName(String name) {
        return roleRepository.findAll();
    }

}
