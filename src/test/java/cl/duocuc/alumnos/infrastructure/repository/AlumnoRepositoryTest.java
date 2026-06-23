package cl.duocuc.ep03.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import cl.duocuc.ep03.infrastructure.entity.AlumnoEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AlumnoRepositoryTest {

    @Autowired private TestEntityManager em;

    @Autowired private AlumnoRepository repo;

    @Test
    void save_persisteYRetornaConId() {
        AlumnoEntity juego = new AlumnoEntity(null, "The Witcher 3", "RPG", "PC");

        AlumnoEntity saved = repo.save(juego);

        assertNotNull(saved.getId());
        assertEquals("The Witcher 3", saved.getTitulo());
        assertEquals("RPG", saved.getGenero());
        assertEquals("PC", saved.getPlataforma());
    }

    @Test
    void findAll_retornaListaConTodosLosJuegos() {
        em.persist(new AlumnoEntity(null, "Elden Ring", "RPG", "PS5"));
        em.persist(new AlumnoEntity(null, "FIFA 24", "Deportes", "Xbox"));
        em.flush();

        List<AlumnoEntity> result = repo.findAll();

        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_retornaJuegoCuandoExiste() {
        AlumnoEntity saved = em.persist(new AlumnoEntity(null, "Minecraft", "Sandbox", "Multi"));
        em.flush();

        Optional<AlumnoEntity> result = repo.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Minecraft", result.get().getTitulo());
    }

    @Test
    void findById_retornaVacioCuandoNoExiste() {
        Optional<AlumnoEntity> result = repo.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteById_eliminaElJuego() {
        AlumnoEntity saved = em.persist(new AlumnoEntity(null, "Hollow Knight", "Indie", "PC"));
        em.flush();
        Long id = saved.getId();

        repo.deleteById(id);
        em.flush();

        Optional<AlumnoEntity> result = repo.findById(id);
        assertFalse(result.isPresent());
    }

    @Test
    void save_actualizaTituloExistente() {
        AlumnoEntity saved = em.persist(new AlumnoEntity(null, "Rocket League", "Deportes", "PC"));
        em.flush();

        saved.setTitulo("Rocket League Actualizado");
        repo.save(saved);
        em.flush();
        em.clear();

        AlumnoEntity updated = repo.findById(saved.getId()).orElseThrow();
        assertEquals("Rocket League Actualizado", updated.getTitulo());
    }
}
