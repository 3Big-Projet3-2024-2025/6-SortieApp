package org.helha.be.sortieappbackend.controllers;

import org.helha.be.sortieappbackend.models.Autorisation;
import org.helha.be.sortieappbackend.services.IAutorisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/Autorisations")
@CrossOrigin(origins = "*")
public class AutorisationController {
    @Autowired
    private IAutorisationService autorisationService;

    @GetMapping("/{id}")
    public ResponseEntity<Autorisation> getAutorisation(int id){
        long start = System.currentTimeMillis();
        Autorisation autorisation = autorisationService.getAutorisations().get(id);
        long end = System.currentTimeMillis();
        System.out.println("Autorisation took " + (end - start) + "ms");
        return ResponseEntity.ok(autorisation);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<Autorisation>> getAutorisations(Pageable page){
        long start = System.currentTimeMillis();
        Page autorisations = autorisationService.getAutorisations(page);
        long end = System.currentTimeMillis();
        System.out.println("Autorisations took " + (end - start) + "ms");
        return ResponseEntity.ok(autorisations);
    }

    @GetMapping
    public ResponseEntity<List<Autorisation>> getAutorisations(){
        long start = System.currentTimeMillis();
        List autorisations = autorisationService.getAutorisations();
        long end = System.currentTimeMillis();
        System.out.println("Autorisations took " + (end - start) + "ms");
        return ResponseEntity.ok(autorisations);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Autorisation>> getAutorisationByUserId(@PathVariable int userId){
        long start = System.currentTimeMillis();
        List autorisations = autorisationService.getAutorisationsByUserID(userId);
        long end = System.currentTimeMillis();
        System.out.println("Autorisations took " + (end - start) + "ms");
        return ResponseEntity.ok(autorisations);
    }

    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<Page<Autorisation>> getAutorisationByUserId(@PathVariable int userId, Pageable page){
        long start = System.currentTimeMillis();
        Page autorisations = autorisationService.getAutorisationsByUserID(userId,page);
        long end = System.currentTimeMillis();
        System.out.println("Autorisations took " + (end - start) + "ms");
        return ResponseEntity.ok(autorisations);
    }

    @PostMapping
    public ResponseEntity<?> addAutorisation(@RequestBody Autorisation autorisation){
        System.out.println("post autorisation");
        try{
            System.out.println("autorisation add"+autorisation);
            return ResponseEntity.status(HttpStatus.CREATED).body(autorisationService.addAutorisation(autorisation));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateAutorisation(@RequestBody Autorisation autorisation){
        try{
            System.out.println("autorisation put"+autorisation);
            return ResponseEntity.status(HttpStatus.CREATED).body(autorisationService.updateAutorisation(autorisation));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAutorisation(@PathVariable long id) {
        autorisationService.deleteAutorisation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/school/{school_id}")
    public ResponseEntity<List<Autorisation>> getAutorisationsBySchool(@PathVariable int school_id){
        long start = System.currentTimeMillis();
        List<Autorisation> autorisations = autorisationService.getAutorisationsBySchoolId(school_id);
        long end = System.currentTimeMillis();
        System.out.println("Autorisations took " + (end - start) + "ms");
        return ResponseEntity.ok(autorisations);
    }

}
