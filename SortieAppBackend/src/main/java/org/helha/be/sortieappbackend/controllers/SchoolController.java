package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.SchoolServiceDB;
import org.helha.be.sortieappbackend.services.UserServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/schools")
@CrossOrigin(origins = "*")
public class SchoolController {

    @Autowired
    private SchoolServiceDB schoolService;

    @Autowired
    private UserServiceDB userService;

    @GetMapping
    public List<School> getSchools() {
        return schoolService.getSchools();
    }

    @PostMapping
    public School addSchool(@RequestBody School school) {
        if (school.getUsers_school() != null) {
            for (User user : school.getUsers_school()) {
                if (user.getId_user() != 0) {
                    User existingUser = userService.getUserById(user.getId_user())
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    user.setSchool_user(school);
                }
            }
        }
        return schoolService.addSchool(school);
    }

    @PutMapping(path = "/{id_school}")
    public School updateSchool(@RequestBody School school, @PathVariable int id_school) {
        if (school.getUsers_school() != null) {
            for (User user : school.getUsers_school()) {
                if (user.getId_user() != 0) {
                    User existingUser = userService.getUserById(user.getId_user())
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    user.setSchool_user(school);
                }
            }
        }
        return schoolService.updateSchool(school, id_school);
    }

    @DeleteMapping(path = "/{id_school}")
    public void deleteSchool(@PathVariable int id_school) {
        schoolService.deleteSchool(id_school);
    }
}
