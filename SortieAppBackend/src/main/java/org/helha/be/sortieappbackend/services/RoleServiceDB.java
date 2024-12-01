package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class RoleServiceDB {

    @Autowired
    private RoleRepository repository;

    public List<Role> getRoles() { return repository.findAll(); }

    public Role addRole(Role role) { return repository.save(role); }

    public Role updateRole(Role newRole, int id_role) {
        return repository.findById(id_role)
                .map(role -> {
                    role.setName_role(newRole.getName_role());
                    return repository.save(role);
                })
                .orElseGet(() -> repository.save(newRole));
    }

    public void deleteRole(int id_role) { repository.deleteById(id_role); }
}
