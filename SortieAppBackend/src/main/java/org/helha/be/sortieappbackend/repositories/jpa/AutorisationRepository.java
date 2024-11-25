package org.helha.be.sortieappbackend.repositories.jpa;

import org.helha.be.sortieappbackend.models.Autorisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AutorisationRepository extends JpaRepository<Autorisation, Long>, PagingAndSortingRepository<Autorisation, Long> {
    //public List<Autorisation> findByUsername(String username);

    //List<Autorisation> findByUser_id(Long userId);
}
