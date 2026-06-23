package cl.duocuc.ep03.cucumber;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duocuc.ep03.domain.Alumno;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AlumnoSteps extends CucumberSpringConfiguration {

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private ObjectMapper objectMapper;

    private ResponseEntity<?> lastResponse;
    private Long lastCreatedId;

    private String baseUrl() {
        return "http://localhost:" + port + "/ep03";
    }

    @Before
    public void limpiarEstado() {
        lastResponse = null;
        lastCreatedId = null;
    }

    @Given("la aplicacion esta en ejecucion")
    public void laAplicacionEstaEnEjecucion() {
        ResponseEntity<String> ping = restTemplate.getForEntity(baseUrl(), String.class);
        assertTrue(ping.getStatusCode().is2xxSuccessful(), "La aplicacion debe estar corriendo");
    }

    @Given("existe un juego con titulo {string} genero {string} y plataforma {string}")
    public void existeUnJuego(String titulo, String genero, String plataforma) {
        Alumno juego = new Alumno(null, titulo, genero, plataforma);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Alumno> request = new HttpEntity<>(juego, headers);

        ResponseEntity<Alumno> response =
                restTemplate.postForEntity(baseUrl(), request, Alumno.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        lastCreatedId = response.getBody().getId();
    }

    @When("consulto la lista de juegos")
    public void consultoLaListaDeJuegos() {
        lastResponse = restTemplate.getForEntity(baseUrl(), List.class);
    }

    @When("creo un juego con titulo {string} genero {string} y plataforma {string}")
    public void creoUnJuego(String titulo, String genero, String plataforma) {
        Alumno juego = new Alumno(null, titulo, genero, plataforma);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Alumno> request = new HttpEntity<>(juego, headers);

        lastResponse = restTemplate.postForEntity(baseUrl(), request, Alumno.class);
        if (lastResponse.getBody() instanceof Alumno a) {
            lastCreatedId = a.getId();
        }
    }

    @When("actualizo el juego con titulo {string} genero {string} y plataforma {string}")
    public void actualizoElJuego(String titulo, String genero, String plataforma) {
        assertNotNull(lastCreatedId, "Debe existir un juego creado previamente");
        Alumno juego = new Alumno(null, titulo, genero, plataforma);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Alumno> request = new HttpEntity<>(juego, headers);

        lastResponse =
                restTemplate.exchange(
                        baseUrl() + "/" + lastCreatedId, HttpMethod.PUT, request, Alumno.class);
    }

    @When("elimino el juego creado")
    public void eliminoElJuegoCreado() {
        assertNotNull(lastCreatedId, "Debe existir un juego creado previamente");
        lastResponse =
                restTemplate.exchange(
                        baseUrl() + "/" + lastCreatedId,
                        HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        Void.class);
    }

    @When("exporto los juegos a CSV")
    public void exportoLosJuegosACSV() {
        lastResponse = restTemplate.getForEntity(baseUrl() + "/export", String.class);
    }

    @When("importo el CSV {string}")
    public void importoElCSV(String csv) {
        String csvContent = csv.replace("\\n", "\n");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> request = new HttpEntity<>(csvContent, headers);

        lastResponse = restTemplate.postForEntity(baseUrl() + "/import", request, Void.class);
    }

    @Then("la respuesta tiene codigo {int}")
    public void laRespuestaTieneCodigo(int codigo) {
        assertNotNull(lastResponse, "Debe haber una respuesta");
        assertEquals(codigo, lastResponse.getStatusCode().value());
    }

    @And("la lista de juegos esta vacia")
    public void laListaDeJuegosEstaVacia() {
        List<?> body = (List<?>) lastResponse.getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty(), "La lista debe estar vacia");
    }

    @And("la lista contiene al menos {int} juego")
    public void laListaContieneAlMenos(int cantidad) {
        List<?> body = (List<?>) lastResponse.getBody();
        assertNotNull(body);
        assertTrue(
                body.size() >= cantidad,
                "La lista debe tener al menos " + cantidad + " elemento(s)");
    }

    @And("el juego retornado tiene titulo {string} genero {string} y plataforma {string}")
    public void elJuegoRetornadoTiene(String titulo, String genero, String plataforma) {
        Alumno juego = objectMapper.convertValue(lastResponse.getBody(), Alumno.class);
        assertNotNull(juego);
        assertEquals(titulo, juego.getTitulo());
        assertEquals(genero, juego.getGenero());
        assertEquals(plataforma, juego.getPlataforma());
    }

    @And("el CSV contiene {string}")
    public void elCSVContiene(String fragmento) {
        String body = (String) lastResponse.getBody();
        assertNotNull(body);
        assertTrue(
                body.contains(fragmento),
                "El CSV debe contener: " + fragmento + "\nCSV actual: " + body);
    }
}
