package cl.duocuc.ep03.infrastructure.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duocuc.ep03.application.AlumnoService;
import cl.duocuc.ep03.domain.Alumno;

@WebMvcTest(AlumnoController.class)
@WithMockUser
class AlumnoControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AlumnoService service;

    @Autowired private ObjectMapper objectMapper;

    @Test
    void listar_retorna200ConLista() throws Exception {
        when(service.listar())
                .thenReturn(
                        List.of(
                                new Alumno(1L, "The Witcher 3", "RPG", "PC"),
                                new Alumno(2L, "Elden Ring", "RPG", "PS5")));

        mockMvc.perform(get("/ep03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("The Witcher 3"))
                .andExpect(jsonPath("$[1].titulo").value("Elden Ring"));
    }

    @Test
    void crear_retorna200ConJuegoCreado() throws Exception {
        Alumno input = new Alumno(null, "FIFA 24", "Deportes", "Xbox");
        Alumno created = new Alumno(3L, "FIFA 24", "Deportes", "Xbox");
        when(service.crear(any(Alumno.class))).thenReturn(created);

        mockMvc.perform(
                        post("/ep03")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.titulo").value("FIFA 24"));
    }

    @Test
    void actualizar_retorna200ConJuegoActualizado() throws Exception {
        Alumno input = new Alumno(null, "Minecraft", "Sandbox", "Multi");
        Alumno updated = new Alumno(4L, "Minecraft", "Sandbox", "Multi");
        when(service.actualizar(eq(4L), any(Alumno.class))).thenReturn(updated);

        mockMvc.perform(
                        put("/ep03/4")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.titulo").value("Minecraft"));
    }

    @Test
    void eliminar_retorna200() throws Exception {
        doNothing().when(service).eliminar(5L);

        mockMvc.perform(delete("/ep03/5").with(csrf())).andExpect(status().isOk());

        verify(service, times(1)).eliminar(5L);
    }

    @Test
    void exportar_retornaCSV() throws Exception {
        when(service.listar())
                .thenReturn(
                        List.of(
                                new Alumno(1L, "The Witcher 3", "RPG", "PC"),
                                new Alumno(2L, "Elden Ring", "RPG", "PS5")));

        mockMvc.perform(get("/ep03/export"))
                .andExpect(status().isOk())
                .andExpect(content().string("The Witcher 3,RPG,PC\nElden Ring,RPG,PS5"));
    }

    @Test
    void exportar_listaVaciaRetornaCadenaVacia() throws Exception {
        when(service.listar()).thenReturn(List.of());

        mockMvc.perform(get("/ep03/export"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void importar_procesaCSVCorrectamente() throws Exception {
        String csv = "Rocket League,Deportes,PC\nHollow Knight,Indie,PC";
        when(service.crear(any(Alumno.class)))
                .thenReturn(new Alumno(1L, "Rocket League", "Deportes", "PC"));

        mockMvc.perform(
                        post("/ep03/import")
                                .with(csrf())
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(csv))
                .andExpect(status().isOk());

        verify(service, times(2)).crear(any(Alumno.class));
    }

    @Test
    void importar_ignoraLineasMalformadas() throws Exception {
        String csv = "SoloTitulo\nRocket League,Deportes,PC";
        when(service.crear(any(Alumno.class)))
                .thenReturn(new Alumno(1L, "Rocket League", "Deportes", "PC"));

        mockMvc.perform(
                        post("/ep03/import")
                                .with(csrf())
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(csv))
                .andExpect(status().isOk());

        verify(service, times(1)).crear(any(Alumno.class));
    }
}
