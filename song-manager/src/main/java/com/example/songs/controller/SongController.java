package com.example.songs.controller;

import com.example.songs.model.Song;
import com.example.songs.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController {
    private final SongService service;

    public SongController(SongService service) {
        this.service = service;
    }

    @GetMapping
    public List<Song> getAll() {
        return service.getAllSongs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSongById(id));
    }

    @PostMapping
    public ResponseEntity<Song> create(@RequestBody Song song) {
        return ResponseEntity.status(201).body(service.createSong(song));
    }

    @PutMapping("/{id}")
    public Song update(@PathVariable Long id, @RequestBody Song song) {
        return service.updateSong(id, song);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}