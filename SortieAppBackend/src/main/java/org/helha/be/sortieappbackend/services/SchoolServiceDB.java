package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.School;
import org.helha.be.sortieappbackend.models.User;
import org.helha.be.sortieappbackend.repositories.jpa.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Primary
public class SchoolServiceDB implements ISchoolService {

    @Autowired
    private SchoolRepository repository;

    @Override
    public List<School> getSchools() {
        return repository.findAll();
    }

    @Override
    public Optional<School> getSchoolById(int id_school) {
        return repository.findById(id_school);
    }

    @Override
    public School addSchool(School school) {
        // Ensure each user points to this school
        if (school.getUsers_school() != null) {
            for (User user : school.getUsers_school()) {
                user.setSchool_user(school);
            }
        }
        return repository.save(school);
    }

    @Override
    public School updateSchool(School newSchool, int id_school) {
        return repository.findById(id_school)
                .map(existingSchool -> {
                    existingSchool.setName_school(newSchool.getName_school());
                    existingSchool.setAddress_school(newSchool.getAddress_school());

                    // Update users
                    if (newSchool.getUsers_school() != null) {
                        for (User user : newSchool.getUsers_school()) {
                            user.setSchool_user(existingSchool);
                        }
                        existingSchool.setUsers_school(newSchool.getUsers_school());
                    }

                    return repository.save(existingSchool);
                })
                .orElseThrow(() -> new RuntimeException("School not found"));
    }

    @Override
    public void deleteSchool(int id_school) {
        repository.deleteById(id_school);
    }
}
