package cl.duocuc.ep03.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import cl.duocuc.ep03.domain.Alumno;
import cl.duocuc.ep03.infrastructure.entity.AlumnoEntity;

class AlumnoMapperTest {

    @Test
    void toDomain_mapeaCorrectamente() {
        AlumnoEntity entity = new AlumnoEntity(1L, "Zelda: TOTK", "Aventura", "Switch");

        Alumno result = AlumnoMapper.toDomain(entity);

        assertEquals(1L, result.getId());
        assertEquals("Zelda: TOTK", result.getTitulo());
        assertEquals("Aventura", result.getGenero());
        assertEquals("Switch", result.getPlataforma());
    }

    @Test
    void toEntity_mapeaCorrectamente() {
        Alumno juego = new Alumno(2L, "Hollow Knight", "Indie", "PC");

        AlumnoEntity result = AlumnoMapper.toEntity(juego);

        assertEquals(2L, result.getId());
        assertEquals("Hollow Knight", result.getTitulo());
        assertEquals("Indie", result.getGenero());
        assertEquals("PC", result.getPlataforma());
    }

    @Test
    void toDomain_conCamposNulos() {
        AlumnoEntity entity = new AlumnoEntity(null, null, null, null);

        Alumno result = AlumnoMapper.toDomain(entity);

        assertNull(result.getId());
        assertNull(result.getTitulo());
        assertNull(result.getGenero());
        assertNull(result.getPlataforma());
    }

    @Test
    void toEntity_conCamposNulos() {
        Alumno juego = new Alumno(null, null, null, null);

        AlumnoEntity result = AlumnoMapper.toEntity(juego);

        assertNull(result.getId());
        assertNull(result.getTitulo());
        assertNull(result.getGenero());
        assertNull(result.getPlataforma());
    }
}
