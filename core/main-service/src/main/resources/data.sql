INSERT INTO users (name, email)
VALUES
    ('Иван Иванов', 'ivan@example.com'),
    ('Анна Смирнова', 'anna@example.com'),
    ('Пётр Соколов', 'petr@example.com'),
    ('Мария Кузнецова', 'maria@example.com'),
    ('Алексей Волков', 'alexey@example.com')
    ON CONFLICT (email) DO NOTHING;

INSERT INTO categories (name)
VALUES
    ('Концерты'),
    ('Выставки'),
    ('Кино'),
    ('Спорт'),
    ('Экскурсии')
    ON CONFLICT (name) DO NOTHING;

/*
INSERT INTO events (
    annotation,
    category_id,
    created_on,
    description,
    event_date,
    initiator_id,
    lat,
    lon,
    paid,
    participant_limit,
    published_on,
    request_moderation,
    title,
    state,
    views
)
VALUES
    (
        'Большой концерт современной рок-группы',
        (SELECT id FROM categories WHERE name = 'Концерты'),
        CURRENT_TIMESTAMP,
        'Вечерний концерт на открытой городской площадке.',
        '2027-06-15 19:00:00',
        (SELECT id FROM users WHERE email = 'ivan@example.com'),
        55.7558,
        37.6173,
        TRUE,
        500,
        CURRENT_TIMESTAMP,
        TRUE,
        'Летний рок-концерт',
        'PUBLISHED',
        125
    ),
    (
        'Концерт классической музыки',
        (SELECT id FROM categories WHERE name = 'Концерты'),
        CURRENT_TIMESTAMP,
        'Произведения известных композиторов в исполнении симфонического оркестра.',
        '2027-07-10 18:30:00',
        (SELECT id FROM users WHERE email = 'anna@example.com'),
        55.7601,
        37.6187,
        TRUE,
        300,
        CURRENT_TIMESTAMP,
        TRUE,
        'Вечер классической музыки',
        'PUBLISHED',
        94
    ),
    (
        'Выставка молодых художников',
        (SELECT id FROM categories WHERE name = 'Выставки'),
        CURRENT_TIMESTAMP,
        'Картины, графика и арт-объекты современных российских художников.',
        '2027-05-20 11:00:00',
        (SELECT id FROM users WHERE email = 'petr@example.com'),
        55.7415,
        37.6208,
        FALSE,
        100,
        CURRENT_TIMESTAMP,
        FALSE,
        'Современное искусство',
        'PUBLISHED',
        58
    ),
    (
        'Экспозиция об истории города',
        (SELECT id FROM categories WHERE name = 'Выставки'),
        CURRENT_TIMESTAMP,
        'Исторические документы, фотографии и предметы городского быта.',
        '2027-08-05 10:00:00',
        (SELECT id FROM users WHERE email = 'maria@example.com'),
        55.7520,
        37.6031,
        TRUE,
        150,
        CURRENT_TIMESTAMP,
        TRUE,
        'История нашего города',
        'PUBLISHED',
        71
    ),
    (
        'Показ нового фантастического фильма',
        (SELECT id FROM categories WHERE name = 'Кино'),
        CURRENT_TIMESTAMP,
        'Премьерный показ фильма с последующим обсуждением.',
        '2027-06-25 20:00:00',
        (SELECT id FROM users WHERE email = 'alexey@example.com'),
        55.7650,
        37.6050,
        TRUE,
        80,
        CURRENT_TIMESTAMP,
        TRUE,
        'Премьера фантастического фильма',
        'PUBLISHED',
        213
    ),
    (
        'Бесплатный показ авторского кино',
        (SELECT id FROM categories WHERE name = 'Кино'),
        CURRENT_TIMESTAMP,
        'Показ независимого фильма и встреча с режиссёром.',
        '2027-09-12 19:30:00',
        (SELECT id FROM users WHERE email = 'ivan@example.com'),
        55.7300,
        37.6400,
        FALSE,
        60,
        CURRENT_TIMESTAMP,
        FALSE,
        'Вечер авторского кино',
        'PUBLISHED',
        47
    ),
    (
        'Городской забег для любителей',
        (SELECT id FROM categories WHERE name = 'Спорт'),
        CURRENT_TIMESTAMP,
        'Дистанция десять километров по центральным улицам города.',
        '2027-05-30 09:00:00',
        (SELECT id FROM users WHERE email = 'anna@example.com'),
        55.7500,
        37.6100,
        TRUE,
        1000,
        CURRENT_TIMESTAMP,
        FALSE,
        'Городской марафон',
        'PUBLISHED',
        356
    ),
    (
        'Открытая тренировка по йоге',
        (SELECT id FROM categories WHERE name = 'Спорт'),
        CURRENT_TIMESTAMP,
        'Занятие подходит участникам с любым уровнем подготовки.',
        '2027-07-18 08:00:00',
        (SELECT id FROM users WHERE email = 'petr@example.com'),
        55.7800,
        37.5900,
        FALSE,
        50,
        CURRENT_TIMESTAMP,
        TRUE,
        'Йога в парке',
        'PUBLISHED',
        82
    ),
    (
        'Пешеходная экскурсия по центру',
        (SELECT id FROM categories WHERE name = 'Экскурсии'),
        CURRENT_TIMESTAMP,
        'Гид расскажет об архитектуре и знаменитых жителях города.',
        '2027-08-20 14:00:00',
        (SELECT id FROM users WHERE email = 'maria@example.com'),
        55.7539,
        37.6208,
        TRUE,
        25,
        CURRENT_TIMESTAMP,
        TRUE,
        'Тайны старого города',
        'PUBLISHED',
        168
    ),
    (
        'Вечерняя экскурсия по набережной',
        (SELECT id FROM categories WHERE name = 'Экскурсии'),
        CURRENT_TIMESTAMP,
        'Прогулка с рассказом об истории набережной и городских мостов.',
        '2027-09-05 20:00:00',
        (SELECT id FROM users WHERE email = 'alexey@example.com'),
        55.7460,
        37.6300,
        TRUE,
        30,
        CURRENT_TIMESTAMP,
        TRUE,
        'Огни вечернего города',
        'PUBLISHED',
        119
    );


INSERT INTO events (
    annotation,
    category_id,
    created_on,
    description,
    event_date,
    initiator_id,
    lat,
    lon,
    paid,
    participant_limit,
    published_on,
    request_moderation,
    title,
    state
)
VALUES
    (
        'Новый музыкальный фестиваль ожидает модерации',
        (SELECT id FROM categories WHERE name = 'Концерты'),
        CURRENT_TIMESTAMP,
        'Фестиваль с участием молодых музыкальных коллективов.',
        '2027-10-10 18:00:00',
        (SELECT id FROM users WHERE email = 'ivan@example.com'),
        55.7558,
        37.6173,
        TRUE,
        400,
        NULL,
        TRUE,
        'Фестиваль молодой музыки',
        'PENDING'
    ),
    (
        'Новая спортивная тренировка ожидает модерации',
        (SELECT id FROM categories WHERE name = 'Спорт'),
        CURRENT_TIMESTAMP,
        'Открытая тренировка по функциональному спорту для начинающих.',
        '2027-11-15 10:00:00',
        (SELECT id FROM users WHERE email = 'anna@example.com'),
        55.7700,
        37.6000,
        FALSE,
        40,
        NULL,
        TRUE,
        'Функциональная тренировка',
        'PENDING'
    ),
    (
        'Отменённая выставка современной фотографии',
        (SELECT id FROM categories WHERE name = 'Выставки'),
        CURRENT_TIMESTAMP,
        'Выставка работ современных городских фотографов была отменена.',
        '2027-10-20 12:00:00',
        (SELECT id FROM users WHERE email = 'petr@example.com'),
        55.7450,
        37.6150,
        TRUE,
        100,
        NULL,
        TRUE,
        'Городская фотография',
        'CANCELED'
    ),
    (
        'Отменённая экскурсия по архитектурным памятникам',
        (SELECT id FROM categories WHERE name = 'Экскурсии'),
        CURRENT_TIMESTAMP,
        'Экскурсия по известным архитектурным памятникам города была отменена.',
        '2027-12-05 15:00:00',
        (SELECT id FROM users WHERE email = 'maria@example.com'),
        55.7505,
        37.6250,
        TRUE,
        20,
        NULL,
        TRUE,
        'Архитектурное наследие',
        'CANCELED'
    );

 */

 /*
  -- 1. Музыкальные события

WITH new_compilation AS (
    INSERT INTO compilations (title, pinned)
    VALUES ('Лучшие музыкальные события', TRUE)
    RETURNING id
)
INSERT INTO compilation_events (compilation_id, event_id)
SELECT new_compilation.id, events.event_id
FROM new_compilation
CROSS JOIN (
    VALUES
        (1::BIGINT),
        (2::BIGINT),
        (15::BIGINT),
        (16::BIGINT)
) AS events(event_id);


-- 2. Выставки и искусство

WITH new_compilation AS (
    INSERT INTO compilations (title, pinned)
    VALUES ('Выставки и искусство', TRUE)
    RETURNING id
)
INSERT INTO compilation_events (compilation_id, event_id)
SELECT new_compilation.id, events.event_id
FROM new_compilation
CROSS JOIN (
    VALUES
        (3::BIGINT),
        (4::BIGINT),
        (17::BIGINT),
        (18::BIGINT)
) AS events(event_id);


-- 3. Кино на вечер

WITH new_compilation AS (
    INSERT INTO compilations (title, pinned)
    VALUES ('Кино на вечер', FALSE)
    RETURNING id
)
INSERT INTO compilation_events (compilation_id, event_id)
SELECT new_compilation.id, events.event_id
FROM new_compilation
CROSS JOIN (
    VALUES
        (5::BIGINT),
        (6::BIGINT),
        (19::BIGINT),
        (20::BIGINT)
) AS events(event_id);


-- 4. Активный отдых

WITH new_compilation AS (
    INSERT INTO compilations (title, pinned)
    VALUES ('Спорт и активный отдых', TRUE)
    RETURNING id
)
INSERT INTO compilation_events (compilation_id, event_id)
SELECT new_compilation.id, events.event_id
FROM new_compilation
CROSS JOIN (
    VALUES
        (7::BIGINT),
        (8::BIGINT),
        (21::BIGINT),
        (22::BIGINT)
) AS events(event_id);


-- 5. Экскурсии по городу

WITH new_compilation AS (
    INSERT INTO compilations (title, pinned)
    VALUES ('Интересные экскурсии', FALSE)
    RETURNING id
)
INSERT INTO compilation_events (compilation_id, event_id)
SELECT new_compilation.id, events.event_id
FROM new_compilation
CROSS JOIN (
    VALUES
        (9::BIGINT),
        (10::BIGINT),
        (23::BIGINT),
        (24::BIGINT)
) AS events(event_id);
  */