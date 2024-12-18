package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.Autorisation;
import org.helha.be.sortieappbackend.services.IAutorisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Autorisations")
public class AutorisationController {
    @Autowired
    private IAutorisationService autorisationService;

    @GetMapping("/paged")
    public Page<Autorisation> getAutorisations(Pageable page){
        long start = System.currentTimeMillis();
        Page autorisations = autorisationService.getAutorisations(page);
        long end = System.currentTimeMillis();
        System.out.println("Autorisations took " + (end - start) + "ms");
        return autorisations;
    }

    @GetMapping
    public List<Autorisation> getAutorisations(){
        long start = System.currentTimeMillis();
        List autorisations = autorisationService.getAutorisations();
        long end = System.currentTimeMillis();
        System.out.println("Autorisations took " + (end - start) + "ms");
        return autorisations;
    }

    @GetMapping("/user/{userId}")
    public List<Autorisation> getAutorisationByUserId(@PathVariable int userId){
        long start = System.currentTimeMillis();
        List autorisations = autorisationService.getAutorisationsByUserID(userId);
        long end = System.currentTimeMillis();
        System.out.println("Autorisations took " + (end - start) + "ms");
        return autorisations;
    }

    @GetMapping("/user/{userId}/paged")
    public Page<Autorisation> getAutorisationByUserId(@PathVariable int userId, Pageable page){
        long start = System.currentTimeMillis();
        Page autorisations = autorisationService.getAutorisationsByUserID(userId,page);
        long end = System.currentTimeMillis();
        System.out.println("Autorisations took " + (end - start) + "ms");
        return autorisations;
    }

    @PostMapping
    public Autorisation addAutorisation(@RequestBody final Autorisation autorisation){ return autorisationService.addAutorisation(autorisation); }

    @PutMapping
    public Autorisation updateAutorisation(@RequestBody final Autorisation autorisation){ return autorisationService.updateAutorisation(autorisation); }

    @DeleteMapping("/{id}")
    public void deleteAutorisation(@PathVariable long id){ autorisationService.deleteAutorisation(id); }

}
