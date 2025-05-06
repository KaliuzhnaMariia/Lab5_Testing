package archtests;

import com.example.songs.model.Song;
import com.example.songs.repository.SongRepository;
import com.example.songs.service.SongService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SongServiceMockTests {

    @Mock
    private SongRepository mockRepository;

    private SongService songService;

    @Captor
    private ArgumentCaptor<Song> songCaptor;

    private Song song;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        songService = new SongService(mockRepository);
        song = new Song("Numb", "Linkin Park", "Meteora", 2003);
    }

    @DisplayName("Get all songs - returns list")
    @Test
    void getAllSongs_shouldReturnAll() {
        List<Song> songs = List.of(song);
        given(mockRepository.findAll()).willReturn(songs);

        List<Song> result = songService.getAllSongs();

        assertThat(result).containsExactly(song);
        verify(mockRepository).findAll();
    }

    @DisplayName("Get song by ID - found")
    @Test
    void getSongById_shouldReturnSong() {
        given(mockRepository.findById(1L)).willReturn(Optional.of(song));

        Song result = songService.getSongById(1L);

        assertThat(result).isEqualTo(song);
        verify(mockRepository).findById(1L);
    }

    @DisplayName("Get song by ID - not found")
    @Test
    void getSongById_shouldThrowIfNotFound() {
        given(mockRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> songService.getSongById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Song not found");

        verify(mockRepository).findById(1L);
    }

    @DisplayName("Create song - success")
    @Test
    void createSong_shouldSaveSong() {
        given(mockRepository.save(song)).willReturn(song);

        Song result = songService.createSong(song);

        assertThat(result).isEqualTo(song);
        verify(mockRepository).save(song);
    }

    @DisplayName("Update song - success")
    @Test
    void updateSong_shouldUpdateFieldsAndSave() {
        Song updated = new Song("In The End", "Linkin Park", "Hybrid Theory", 2000);
        given(mockRepository.findById(1L)).willReturn(Optional.of(song));
        given(mockRepository.save(any(Song.class))).willReturn(updated);

        Song result = songService.updateSong(1L, updated);

        assertThat(result.getTitle()).isEqualTo("In The End");
        assertThat(result.getAlbum()).isEqualTo("Hybrid Theory");
        verify(mockRepository).save(song);
    }

    @DisplayName("Update song - not found")
    @Test
    void updateSong_shouldThrowIfNotFound() {
        given(mockRepository.findById(999L)).willReturn(Optional.empty());

        Song update = new Song("Whatever", "Artist", "Album", 2020);

        assertThatThrownBy(() -> songService.updateSong(999L, update))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Song not found");

        verify(mockRepository, never()).save(any());
    }

    @DisplayName("Delete song - by ID")
    @Test
    void deleteSong_shouldCallRepository() {
        songService.deleteSong(1L);

        verify(mockRepository).deleteById(1L);
    }

    @DisplayName("Create song - capture saved song")
    @Test
    void createSong_shouldCaptureSavedSong() {
        given(mockRepository.save(any(Song.class))).willReturn(song);

        songService.createSong(song);

        verify(mockRepository).save(songCaptor.capture());
        Song saved = songCaptor.getValue();

        assertThat(saved.getTitle()).isEqualTo("Numb");
        assertThat(saved.getArtist()).isEqualTo("Linkin Park");
    }

    @DisplayName("Find by title - optional found")
    @Test
    void findFirstByTitle_shouldReturnIfExists() {
        given(mockRepository.findFirstByTitle("Numb")).willReturn(Optional.of(song));

        Optional<Song> result = mockRepository.findFirstByTitle("Numb");

        assertThat(result).isPresent();
        assertThat(result.get().getArtist()).isEqualTo("Linkin Park");
    }

    @DisplayName("Delete song - verify not called if ID is null")
    @Test
    void deleteSong_shouldNotDeleteIfIdIsNull() {
        assertThrows(NullPointerException.class, () -> songService.deleteSong(null));
        verify(mockRepository, never()).deleteById(any());
    }
}
