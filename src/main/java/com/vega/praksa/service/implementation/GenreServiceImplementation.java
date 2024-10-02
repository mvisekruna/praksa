package com.vega.praksa.service.implementation;

import com.vega.praksa.model.Genre;
import com.vega.praksa.repository.GenreRepository;
import com.vega.praksa.service.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreServiceImplementation implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre findById(Long id) {
        return genreRepository.findById(id).orElse(null);
    }

    @Override
    public Genre findByName(String name) {
        return genreRepository.findByName(name);
    }

    @Override
    public void saveGenre(Genre genre) {
        genreRepository.save(genre);
    }

}
