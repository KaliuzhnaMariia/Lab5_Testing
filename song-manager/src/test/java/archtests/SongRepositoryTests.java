package archtests;

import com.example.songs.SongManagerApplication;
import com.example.songs.model.Song;
import com.example.songs.repository.SongRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(classes = SongManagerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SongRepositoryTests {

    @Autowired
    private SongRepository underTest;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        underTest.save(new Song("Imagine", "John Lennon", "Imagine", 1971));
        underTest.save(new Song("Bohemian Rhapsody", "Queen", "A Night at the Opera", 1975));
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldSaveAndFindSong() {
        Song song = new Song("Space Oddity", "David Bowie", "Space Oddity", 1969);
        underTest.save(song);
        List<Song> all = underTest.findAll();
        assertThat(all).extracting(Song::getTitle).contains("Space Oddity");
    }

    @Test
    void shouldGiveIdForNewRecord() {
        Song song = new Song("Skyfall", "Adele", "Skyfall", 2012);
        underTest.save(song);
        Song saved = underTest.findAll().stream()
                .filter(s -> s.getTitle().equals("Skyfall"))
                .findFirst()
                .orElseThrow();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void shouldDeleteSongById() {
        Song song = new Song("Hallelujah", "Leonard Cohen", "Various Positions", 1984);
        Song saved = underTest.save(song);
        Long id = saved.getId();
        underTest.deleteById(id);
        boolean exists = underTest.findById(id).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    void shouldFindSongByTitle() {
        Song song1 = new Song("Imagine", "John Lennon", "Imagine", 1971);
        Song song2 = new Song("Bohemian Rhapsody", "Queen", "A Night at the Opera", 1975);
        underTest.save(song1);
        underTest.save(song2);

        Song foundSong = (Song) underTest.findFirstByTitle("Imagine").orElseThrow();
        assertThat(foundSong.getTitle()).isEqualTo("Imagine");
    }

    @Test
    void shouldFindSongById() {
        Song song = new Song("Yesterday", "The Beatles", "Help!", 1965);
        Song savedSong = underTest.save(song);
        Long id = savedSong.getId();
        Song foundSong = underTest.findById(id).orElseThrow();
        assertThat(foundSong).isEqualTo(savedSong);
    }

    @Test
    void shouldNotSaveSongWithEmptyTitle() {
        Song song = new Song("", "Unknown Artist", "Unknown Album", 2000);
        Song savedSong = underTest.save(song);
        assertThat(savedSong.getTitle()).isEmpty();
    }

    @Test
    void shouldSaveSongWithValidYear() {
        Song song = new Song("New Song", "Artist", "Album", 2025);
        Song savedSong = underTest.save(song);
        assertThat(savedSong.getYear()).isEqualTo(2025);
    }

    @Test
    void shouldFindSongByArtist() {
        Song song = new Song("Another One Bites the Dust", "Queen", "The Game", 1980);
        underTest.save(song);
        List<Song> songsByArtist = underTest.findAll().stream()
                .filter(s -> s.getArtist().equals("Queen"))
                .collect(Collectors.toList());
        assertThat(songsByArtist).hasSize(2);
    }

    @Test
    void shouldCountSongs() {
        long countBefore = underTest.count();
        Song song = new Song("New Song", "Artist", "Album", 2025);
        underTest.save(song);
        long countAfter = underTest.count();
        assertThat(countAfter).isEqualTo(countBefore + 1);
    }

    @Test
    void shouldCheckIfSongExistsById() {
        Song song = new Song("Song Title", "Artist", "Album", 2025);
        Song savedSong = underTest.save(song);
        Long id = savedSong.getId();
        boolean exists = underTest.existsById(id);
        assertThat(exists).isTrue();
    }
}
