-- 1. Categorías oficiales
INSERT INTO categories (nombre, descripcion) VALUES
                                                 ('Conciertos', 'Presentaciones musicales en vivo y recitales'),
                                                 ('Talleres', 'Sesiones prácticas e interactivas de aprendizaje'),
                                                 ('Conferencias', 'Charlas magistrales, congresos y simposios'),
                                                 ('Deportes', 'Torneos, carreras y competencias atléticas'),
                                                 ('Gastronomía', 'Ferias culinarias, catas y experiencias gastronómicas'),
                                                 ('Festivales', 'Celebraciones culturales y artísticas de varios días'),
                                                 ('Teatro', 'Obras dramáticas, comedias y artes escénicas');

-- 2. Sedes en distintas ciudades
INSERT INTO venues (nombre, direccion, capacidad, ciudad) VALUES
                                                              ('Movistar Arena', 'Dg. 61c #26-36', 14000, 'Bogotá'),
                                                              ('Centro de Convenciones Ágora', 'Ac. 24 #38-47', 4000, 'Bogotá'),
                                                              ('Plaza Mayor', 'Cl. 41 #55-80', 3500, 'Medellín'),
                                                              ('Centro de Eventos Puerta de Oro', 'Vía 40 #79B-06', 8000, 'Barranquilla'),
                                                              ('Teatro Jorge Isaacs', 'Cra. 3 #12-28', 1200, 'Cali'),
                                                              ('Centro de Convenciones Cartagena', 'Cl. 24 #8A-344', 2500, 'Cartagena');

-- 3. Evento explícito para validar el Escenario 6 (Búsqueda insensible y parcial)
INSERT INTO events (nombre, fecha, descripcion, activo, venue_id)
VALUES ('Concierto de ROCK', CURRENT_DATE + INTERVAL '15 days', 'Gran noche de rock clásico y alternativo en vivo', TRUE, 1);

INSERT INTO events_categories (event_id, category_id) VALUES (1, 1), (1, 6);

-- 4. Generación masiva de 200 eventos variados
INSERT INTO events (nombre, fecha, descripcion, activo, venue_id)
SELECT
    'Evento Eventify #' || g || ' - ' ||
    (ARRAY['Cumbre Tech', 'Festival Musical', 'Workshop IA', 'Feria Gourmet', 'Gala de Teatro', 'Maratón Ciudad'])[1 + (g % 6)],
    CURRENT_DATE + ((g % 180) || ' days')::INTERVAL,
    'Descripción oficial del evento número ' || g || ' organizado en la plataforma Eventify.',
    TRUE,
    1 + (g % 6)
FROM generate_series(2, 201) AS g;

-- 5. Asignación de categorías a los 200 eventos
INSERT INTO events_categories (event_id, category_id)
SELECT id, 1 + (id % 7) FROM events WHERE id > 1;

INSERT INTO events_categories (event_id, category_id)
SELECT id, 1 + ((id + 2) % 7) FROM events WHERE id > 1 AND id % 2 = 0;