package cl.duocuc.ep03.infrastructure.mapper;

import cl.duocuc.ep03.domain.Alumno;
import cl.duocuc.ep03.infrastructure.entity.AlumnoEntity;

public final class AlumnoMapper {

    private AlumnoMapper() {
        // clase de utilidad, no instanciar
    }

    public static Alumno toDomain(AlumnoEntity e) {
        return new Alumno(e.getId(), e.getTitulo(), e.getGenero(), e.getPlataforma());
    }

    public static AlumnoEntity toEntity(Alumno a) {
        return new AlumnoEntity(a.getId(), a.getTitulo(), a.getGenero(), a.getPlataforma());
    }
}
