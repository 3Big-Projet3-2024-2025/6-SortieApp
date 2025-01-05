package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.services.SchoolServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/schools")
@CrossOrigin(origins = "*")
public class SchoolController {

    @Autowired
    private SchoolServiceDB schoolService;

    /**
     * Retrieves all schools.
     *
     * @return a list of schools.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE', 'LOCAL_ADMIN')")
    @GetMapping
    public List<School> getSchools() {
        return schoolService.getSchools();
    }

    /**
     * Retrieves the list of users associated with a specific school.
     *
     * @param id_school the ID of the school
     * @return a response containing the list of users or a 404 status if the school is not found
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE', 'LOCAL_ADMIN')")
    @GetMapping("/getUsersBySchool/{id_school}")
    public ResponseEntity<List<User>> getUsersBySchool(@PathVariable int id_school) {
        Optional<School> school = schoolService.getSchoolById(id_school);

        if (school.isPresent()) {
            List<User> users = school.get().getUsers_school();
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE', 'LOCAL_ADMIN')")
    @GetMapping("/getStudentsBySchool/{id_school}")
    public ResponseEntity<List<User>> getStudentsBySchool(@PathVariable int id_school) {
        Optional<School> school = schoolService.getSchoolById(id_school);

        if (school.isPresent()) {
            List<User> users = school.get().getUsers_school().stream()
                    .filter(user -> "student".equalsIgnoreCase(user.getRole_user().getName_role()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE', 'LOCAL_ADMIN')")
    @GetMapping("/getSupervisorBySchool/{id_school}")
    public ResponseEntity<List<User>> getSupervisorBySchool(@PathVariable int id_school) {
        Optional<School> school = schoolService.getSchoolById(id_school);

        if (school.isPresent()) {
            List<User> users = school.get().getUsers_school().stream()
                    .filter(user -> "supervisor".equalsIgnoreCase(user.getRole_user().getName_role()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Adds a new school.
     *
     * @param school the school to add.
     * @return the added school.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE', 'LOCAL_ADMIN')")
    @PostMapping
    public School addSchool(@RequestBody School school) {
        return schoolService.addSchool(school);
    }

    /**
     * Updates an existing school.
     *
     * @param school    the updated school data.
     * @param id_school the ID of the school to update.
     * @return the updated school.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE', 'LOCAL_ADMIN')")
    @PutMapping(path = "/{id_school}")
    public School updateSchool(@RequestBody School school, @PathVariable int id_school) {
        return schoolService.updateSchool(school, id_school);
    }


    /**
     * Deletes a school by ID.
     *
     * @param id_school the ID of the school to delete.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE', 'LOCAL_ADMIN')")
    @DeleteMapping(path = "/{id_school}")
    public void deleteSchool(@PathVariable int id_school) {
        schoolService.deleteSchool(id_school);
    }
}
