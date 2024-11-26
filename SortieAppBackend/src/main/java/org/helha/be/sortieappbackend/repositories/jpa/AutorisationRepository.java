package org.helha.be.sortieappbackend.repositories.jpa;

import org.helha.be.sortieappbackend.models.Autorisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AutorisationRepository extends JpaRepository<Autorisation, Long>, PagingAndSortingRepository<Autorisation, Long> {
    //public List<Autorisation> findByUsername(String username);

    List<Autorisation> findByUser_id(int userId);
    Page<Autorisation> findByUser_id(int userId, Pageable page);
}