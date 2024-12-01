package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.Role;

import java.util.List;

public interface IRoleService {
    public List<Role> getRoles();
    public Role addRole(Role role);
    public Role updateRole(Role role, int id_role);
    public void deleteRole(int id_role);
}
