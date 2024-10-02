package com.vega.praksa.controller;

import com.vega.praksa.exception.ResourceConflictException;
import com.vega.praksa.model.Genre;
import com.vega.praksa.service.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/genres", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/all")
    public List<Genre> findAll() {
        return this.genreService.findAll();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable Long id) {
        return this.genreService.findById(id);
    }

    @GetMapping("/name")
    public Genre findByName(@RequestParam("name") String name) {
        return this.genreService.findByName(name);
    }

    @PostMapping("/add-genre")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> saveGenre(@RequestBody Genre genre) {
        Genre existingGenre = this.genreService.findByName(genre.getName());

        if( existingGenre != null) {
            throw new ResourceConflictException(genre.getId(), "Genre already exists.");
        }

        this.genreService.saveGenre(genre);

        return new ResponseEntity<>("Genre saved successfully.", HttpStatus.OK);
    }

}
