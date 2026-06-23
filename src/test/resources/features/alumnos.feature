Feature: Gestion del catalogo de videojuegos
  Como usuario del sistema
  Quiero gestionar videojuegos mediante la API REST
  Para mantener el catalogo actualizado

  Background:
    Given la aplicacion esta en ejecucion

  Scenario: Listar juegos cuando no hay ninguno
    When consulto la lista de juegos
    Then la respuesta tiene codigo 200
    And la lista de juegos esta vacia

  Scenario: Crear un juego exitosamente
    When creo un juego con titulo "The Witcher 3" genero "RPG" y plataforma "PC"
    Then la respuesta tiene codigo 200
    And el juego retornado tiene titulo "The Witcher 3" genero "RPG" y plataforma "PC"

  Scenario: Listar juegos despues de crear uno
    Given existe un juego con titulo "Elden Ring" genero "RPG" y plataforma "PS5"
    When consulto la lista de juegos
    Then la respuesta tiene codigo 200
    And la lista contiene al menos 1 juego

  Scenario: Actualizar un juego existente
    Given existe un juego con titulo "FIFA 24" genero "Deportes" y plataforma "Xbox"
    When actualizo el juego con titulo "FIFA 24" genero "Deportes" y plataforma "Multi"
    Then la respuesta tiene codigo 200
    And el juego retornado tiene titulo "FIFA 24" genero "Deportes" y plataforma "Multi"

  Scenario: Eliminar un juego existente
    Given existe un juego con titulo "Hollow Knight" genero "Indie" y plataforma "PC"
    When elimino el juego creado
    Then la respuesta tiene codigo 200

  Scenario: Exportar juegos a CSV
    Given existe un juego con titulo "Minecraft" genero "Sandbox" y plataforma "Multi"
    When exporto los juegos a CSV
    Then la respuesta tiene codigo 200
    And el CSV contiene "Minecraft,Sandbox,Multi"

  Scenario: Importar juegos desde CSV
    When importo el CSV "Rocket League,Deportes,PC\nZelda TOTK,Aventura,Switch"
    Then la respuesta tiene codigo 200
