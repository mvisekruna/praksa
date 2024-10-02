package com.vega.praksa.service;

import com.vega.praksa.model.Genre;

import java.util.List;

public interface GenreService {

    List<Genre> findAll();
    Genre findById(Long id);
    Genre findByName(String name);
    void saveGenre(Genre genre);

}
