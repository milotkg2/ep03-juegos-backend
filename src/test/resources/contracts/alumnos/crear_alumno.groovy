import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "POST /ep03 crea un juego y retorna el objeto con id"

    request {
        method POST()
        url '/ep03'
        headers {
            contentType applicationJson()
        }
        body([
            titulo    : "The Witcher 3",
            genero    : "RPG",
            plataforma: "PC"
        ])
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            id        : $(producer(anyPositiveInt()), consumer(1)),
            titulo    : "The Witcher 3",
            genero    : "RPG",
            plataforma: "PC"
        ])
    }
}
