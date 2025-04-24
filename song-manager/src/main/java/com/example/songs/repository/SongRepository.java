package com.example.songs.repository;

import com.example.songs.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    Optional<Song> findFirstByTitle(String title);
}
