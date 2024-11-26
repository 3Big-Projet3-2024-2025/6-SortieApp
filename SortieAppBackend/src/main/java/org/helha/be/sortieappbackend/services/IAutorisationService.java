package org.helha.be.sortieappbackend.services;

import org.helha.be.sortieappbackend.models.Autorisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IAutorisationService {
    public Page<Autorisation> getAutorisationsByUserID(int userId, Pageable page);
    public List<Autorisation> getAutorisationsByUserID(int userId);
    public Page<Autorisation> getAutorisations(Pageable page);
    public List<Autorisation> getAutorisations();
    public Autorisation addAutorisation(Autorisation autorisation);
    public Autorisation updateAutorisation(Autorisation autorisation);
    public void deleteAutorisation(long id);
}
