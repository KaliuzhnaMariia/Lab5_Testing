package archtests;

import com.example.songs.model.Song;
import com.example.songs.repository.SongRepository;
import com.example.songs.service.SongService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SongServiceTests {

    private SongRepository songRepository;
    private SongService songService;

    @BeforeEach
    void setUp() {
        songRepository = mock(SongRepository.class);
        songService = new SongService(songRepository);
    }

    @Test
    void shouldReturnAllSongs() {
        List<Song> songs = List.of(new Song("Imagine", "John Lennon", "Imagine", 1971));
        when(songRepository.findAll()).thenReturn(songs);

        List<Song> result = songService.getAllSongs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Imagine");
    }

    @Test
    void shouldReturnSongById() {
        Song song = new Song("Imagine", "John Lennon", "Imagine", 1971);
        when(songRepository.findById(1L)).thenReturn(Optional.of(song));

        Song result = songService.getSongById(1L);

        assertThat(result.getTitle()).isEqualTo("Imagine");
    }

    @Test
    void shouldThrowWhenSongNotFoundById() {
        when(songRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> songService.getSongById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Song not found");
    }

    @Test
    void shouldCreateNewSong() {
        Song song = new Song("Hey Jude", "The Beatles", "Hey Jude", 1968);
        when(songRepository.save(song)).thenReturn(song);

        Song created = songService.createSong(song);

        assertThat(created).isEqualTo(song);
    }

    @Test
    void shouldUpdateExistingSong() {
        Song existing = new Song("Imagine", "John Lennon", "Imagine", 1971);
        when(songRepository.findById(1L)).thenReturn(Optional.of(existing));

        Song updated = new Song("Imagine Updated", "John Lennon", "Imagine Deluxe", 1972);
        when(songRepository.save(any(Song.class))).thenReturn(updated);

        Song result = songService.updateSong(1L, updated);

        assertThat(result.getTitle()).isEqualTo("Imagine Updated");
        assertThat(result.getAlbum()).isEqualTo("Imagine Deluxe");
    }

    @Test
    void shouldThrowWhenUpdateNonExistingSong() {
        Song newSong = new Song("New Title", "Artist", "Album", 2000);
        when(songRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> songService.updateSong(100L, newSong))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldDeleteSongById() {
        doNothing().when(songRepository).deleteById(1L);

        songService.deleteSong(1L);

        verify(songRepository, times(1)).deleteById(1L);
    }

    @Test
    void createSongShouldCallRepository() {
        Song song = new Song("Test", "Artist", "Album", 2020);
        songService.createSong(song);

        verify(songRepository).save(song);
    }

    @Test
    void updateShouldModifyFieldsCorrectly() {
        Song original = new Song("Old", "A", "B", 1990);
        when(songRepository.findById(1L)).thenReturn(Optional.of(original));

        Song updated = new Song("New", "New Artist", "New Album", 2021);
        songService.updateSong(1L, updated);

        ArgumentCaptor<Song> captor = ArgumentCaptor.forClass(Song.class);
        verify(songRepository).save(captor.capture());
        Song saved = captor.getValue();

        assertThat(saved.getTitle()).isEqualTo("New");
        assertThat(saved.getArtist()).isEqualTo("New Artist");
    }

    @Test
    void getAllSongsShouldReturnEmptyList() {
        when(songRepository.findAll()).thenReturn(Collections.emptyList());

        List<Song> result = songService.getAllSongs();

        assertThat(result).isEmpty();
    }

    @Test
    void deleteShouldNotThrowWhenSongExists() {
        doNothing().when(songRepository).deleteById(2L);
        songService.deleteSong(2L);
        verify(songRepository).deleteById(2L);
    }

    @Test
    void createShouldReturnSavedSong() {
        Song song = new Song("Test", "Artist", "Album", 2023);
        when(songRepository.save(song)).thenReturn(song);

        Song result = songService.createSong(song);

        assertThat(result).isEqualTo(song);
    }

    @Test
    void updateShouldSaveUpdatedEntity() {
        Song existing = new Song("A", "B", "C", 2000);
        when(songRepository.findById(5L)).thenReturn(Optional.of(existing));

        Song update = new Song("Z", "Y", "X", 2025);
        songService.updateSong(5L, update);

        verify(songRepository).save(any(Song.class));
    }

    @Test
    void getByIdShouldReturnCorrectEntity() {
        Song song = new Song("Track", "Artist", "Album", 1999);
        when(songRepository.findById(999L)).thenReturn(Optional.of(song));

        Song result = songService.getSongById(999L);

        assertThat(result.getTitle()).isEqualTo("Track");
    }

    @Test
    void multipleSongsCanBeReturned() {
        List<Song> list = List.of(
                new Song("One", "A", "B", 1991),
                new Song("Two", "C", "D", 1992)
        );
        when(songRepository.findAll()).thenReturn(list);

        List<Song> result = songService.getAllSongs();

        assertThat(result).hasSize(2);
    }

    @Test
    void deleteShouldBeCalledOnce() {
        songService.deleteSong(123L);
        verify(songRepository, times(1)).deleteById(123L);
    }

    @Test
    void updateShouldRetainId() {
        Song existing = new Song("T", "A", "A", 2001);
        existing.setId(7L);
        when(songRepository.findById(7L)).thenReturn(Optional.of(existing));

        Song updated = new Song("X", "Y", "Z", 2020);

        Song saved = new Song("X", "Y", "Z", 2020);
        saved.setId(7L);
        when(songRepository.save(any(Song.class))).thenReturn(saved);

        Song result = songService.updateSong(7L, updated);

        assertThat(result.getId()).isEqualTo(7L);
    }

    @Test
    void getSongByIdShouldThrowWithMessage() {
        when(songRepository.findById(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> songService.getSongById(55L))
                .hasMessageContaining("Song not found");
    }

    @Test
    void updateShouldCallFindByIdOnce() {
        Song existing = new Song("A", "B", "C", 2000);
        when(songRepository.findById(1L)).thenReturn(Optional.of(existing));

        Song updated = new Song("D", "E", "F", 2020);
        songService.updateSong(1L, updated);

        verify(songRepository, times(1)).findById(1L);
    }

    @Test
    void updateShouldCallSaveOnceAndPreserveId() {
        Song existing = new Song("Title", "Artist", "Album", 1995);
        existing.setId(10L);
        when(songRepository.findById(10L)).thenReturn(Optional.of(existing));

        Song update = new Song("Title", "Artist", "Album", 1995);
        Song saved = new Song("Title", "Artist", "Album", 1995);
        saved.setId(10L);
        when(songRepository.save(any(Song.class))).thenReturn(saved);

        Song result = songService.updateSong(10L, update);

        assertThat(result.getId()).isEqualTo(10L);
        verify(songRepository, times(1)).save(any(Song.class));
    }

}
