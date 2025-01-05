package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.Role;
import org.helha.be.sortieappbackend.services.RoleServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    RoleServiceDB serviceDB;

    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE', 'LOCAL_ADMIN')")
    @GetMapping
    public List<Role> getRoles() { return serviceDB.getRoles(); }

    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE', 'LOCAL_ADMIN')")
    @PostMapping
    public Role addRole(@RequestBody Role role) { return serviceDB.addRole(role); }

    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE', 'LOCAL_ADMIN')")
    @PutMapping(path="/{id_role}")
    public Role updateRole(@RequestBody Role role, @PathVariable int id_role) { return serviceDB.updateRole(role, id_role); }

    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE', 'LOCAL_ADMIN')")
    @DeleteMapping(path="/{id_role}")
    public void deleteRole(@PathVariable int id_role) { serviceDB.deleteRole(id_role); }
}
