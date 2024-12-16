package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.School;

import java.util.List;
import java.util.Optional;

public interface ISchoolService {
    List<School> getSchools();
    Optional<School> getSchoolById(int id_school);
    School addSchool(School school);
    School updateSchool(School school, int id_school);
    void deleteSchool(int id_school);
}
