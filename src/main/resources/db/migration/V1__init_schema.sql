CREATE TABLE venues (
                        id BIGSERIAL PRIMARY KEY,
                        nombre VARCHAR(120) NOT NULL,
                        direccion VARCHAR(255) NOT NULL,
                        capacidad INTEGER NOT NULL CHECK (capacidad > 0)
);

CREATE TABLE events (
                        id BIGSERIAL PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        fecha DATE NOT NULL,
                        descripcion VARCHAR(500)
);