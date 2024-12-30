package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.Autorisation;
import org.helha.be.sortieappbackend.repositories.jpa.AutorisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class AutorisationServiceDB implements IAutorisationService {

    @Autowired
    private AutorisationRepository autorisationRepository;

    public Page<Autorisation> getAutorisationsByUserID(int userId,Pageable page) {
        return autorisationRepository.findByUser_id(userId,page);
    }

    @Override
    public List<Autorisation> getAutorisationsByUserID(int userId) {
        return autorisationRepository.findByUser_id(userId);
    }

    public Page<Autorisation> getAutorisations(Pageable page) {
        return autorisationRepository.findAll((org.springframework.data.domain.Pageable) page);
    }


    public List<Autorisation> getAutorisations() {
        return autorisationRepository.findAll();
    }


    public Autorisation addAutorisation(Autorisation autorisation) {
        return autorisationRepository.save(autorisation);
    }


    public Autorisation updateAutorisation(Autorisation autorisation) {return autorisationRepository.save(autorisation);}


    public void deleteAutorisation(long id) {
        autorisationRepository.deleteById(id);
    }
}
