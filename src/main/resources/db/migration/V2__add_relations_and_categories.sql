-- 1. Evolución geográfica en Venues
ALTER TABLE venues ADD COLUMN ciudad VARCHAR(100) NOT NULL DEFAULT 'Bogotá';

-- 2. Soft Delete y relación Many-to-One en Events
ALTER TABLE events ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE events ADD COLUMN venue_id BIGINT NOT NULL;

ALTER TABLE events
    ADD CONSTRAINT fk_events_venue
        FOREIGN KEY (venue_id) REFERENCES venues (id);

-- 3. Nueva entidad Category
CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            nombre VARCHAR(80) NOT NULL UNIQUE,
                            descripcion VARCHAR(255)
);

-- 4. Tabla intermedia explícita Many-to-Many
CREATE TABLE events_categories (
                                   event_id BIGINT NOT NULL,
                                   category_id BIGINT NOT NULL,
                                   PRIMARY KEY (event_id, category_id),
                                   CONSTRAINT fk_ec_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
                                   CONSTRAINT fk_ec_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);

-- Índices para acelerar filtros y ordenamiento cronológico
CREATE INDEX idx_events_fecha_desc ON events (fecha DESC);
CREATE INDEX idx_events_activo ON events (activo);
CREATE INDEX idx_venues_ciudad ON venues (ciudad);