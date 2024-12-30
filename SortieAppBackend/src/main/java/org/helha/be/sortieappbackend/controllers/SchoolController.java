package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.services.SchoolServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public List<School> getSchools() {
        return schoolService.getSchools();
    }

    /**
     * Adds a new school.
     *
     * @param school the school to add.
     * @return the added school.
     */
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
    @PutMapping(path = "/{id_school}")
    public School updateSchool(@RequestBody School school, @PathVariable int id_school) {
        return schoolService.updateSchool(school, id_school);
    }

    /**
     * Deletes a school by ID.
     *
     * @param id_school the ID of the school to delete.
     */
    @DeleteMapping(path = "/{id_school}")
    public void deleteSchool(@PathVariable int id_school) {
        schoolService.deleteSchool(id_school);
    }
}
