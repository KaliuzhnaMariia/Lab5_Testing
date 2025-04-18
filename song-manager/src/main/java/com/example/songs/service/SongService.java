package com.example.songs.service;

import com.example.songs.model.Song;
import com.example.songs.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {
    private final SongRepository repository;

    public SongService(SongRepository repository) {
        this.repository = repository;
    }

    public List<Song> getAllSongs() {
        return repository.findAll();
    }

    public Song getSongById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Song not found"));
    }

    public Song createSong(Song song) {
        return repository.save(song);
    }

    public Song updateSong(Long id, Song newSong) {
        Song song = getSongById(id);
        song.setTitle(newSong.getTitle());
        song.setArtist(newSong.getArtist());
        song.setAlbum(newSong.getAlbum());
        song.setYear(newSong.getYear());
        return repository.save(song);
    }

    public void deleteSong(Long id) {
        repository.deleteById(id);
    }
}