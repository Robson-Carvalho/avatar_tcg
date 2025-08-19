CREATE EXTENSION IF NOT EXISTS "pgcrypto";

INSERT INTO cards (id, user_id, name, element, phase, attack, life, defense, rarity)
VALUES
    (gen_random_uuid(), NULL, 'Soldado da Tribo da Água', 'WATER', 'YOUNG', 50, 55, 45, 'COMMON'),
    (gen_random_uuid(), NULL, 'Soldado da Nação do Fogo', 'FIRE', 'YOUNG', 55, 50, 40, 'COMMON'),
    (gen_random_uuid(), NULL, 'Camponês da Terra', 'EARTH', 'YOUNG', 45, 60, 50, 'COMMON'),
    (gen_random_uuid(), NULL, 'Monge do Ar', 'AIR', 'YOUNG', 52, 52, 48, 'COMMON'),
    (gen_random_uuid(), NULL, 'Aprendiz de Água', 'WATER', 'YOUNG', 55, 53, 47, 'COMMON'),
    (gen_random_uuid(), NULL, 'Aprendiz de Fogo', 'FIRE', 'YOUNG', 60, 45, 40, 'COMMON'),
    (gen_random_uuid(), NULL, 'Aprendiz de Terra', 'EARTH', 'YOUNG', 53, 55, 52, 'COMMON'),
    (gen_random_uuid(), NULL, 'Aprendiz de Ar', 'AIR', 'YOUNG', 52, 52, 48, 'COMMON'),
    (gen_random_uuid(), NULL, 'Soldado da Terra', 'EARTH', 'YOUNG', 54, 54, 50, 'COMMON'),
    (gen_random_uuid(), NULL, 'Caçador da Água', 'WATER', 'YOUNG', 53, 50, 45, 'COMMON'),
    (gen_random_uuid(), NULL, 'Guerreiro da Água', 'WATER', 'YOUNG', 50, 52, 48, 'COMMON'),
    (gen_random_uuid(), NULL, 'Aprendiz de Fogo II', 'FIRE', 'YOUNG', 52, 54, 46, 'COMMON'),
    (gen_random_uuid(), NULL, 'Guardião da Terra', 'EARTH', 'YOUNG', 50, 53, 52, 'COMMON'),
    (gen_random_uuid(), NULL, 'Acrobata do Ar', 'AIR', 'YOUNG', 54, 45, 42, 'COMMON'),

-- RARES (25-30% mais fortes que COMMON)
    (gen_random_uuid(), NULL, 'Aang Jovem', 'AIR', 'YOUNG', 70, 60, 55, 'RARE'),
    (gen_random_uuid(), NULL, 'Katara Adulta', 'WATER', 'ADULT', 75, 75, 65, 'RARE'),
    (gen_random_uuid(), NULL, 'Zuko Adulto', 'FIRE', 'ADULT', 80, 75, 70, 'RARE'),
    (gen_random_uuid(), NULL, 'Toph Metal', 'METAL', 'ADULT', 75, 80, 75, 'RARE'),
    (gen_random_uuid(), NULL, 'Azula Jovem', 'FIRE', 'YOUNG', 80, 65, 60, 'RARE'),
    (gen_random_uuid(), NULL, 'Korra Jovem', 'WATER', 'YOUNG', 75, 70, 65, 'RARE'),
    (gen_random_uuid(), NULL, 'Kuvira', 'METAL', 'ADULT', 75, 85, 80, 'RARE'),

-- EPICS (50-60% mais fortes que COMMON)
    (gen_random_uuid(), NULL, 'Aang Adulto', 'AIR', 'ADULT', 100, 95, 85, 'EPIC'),
    (gen_random_uuid(), NULL, 'Katara Dobra de Sangue', 'BLOOD', 'MASTER', 110, 90, 80, 'EPIC'),
    (gen_random_uuid(), NULL, 'Iroh Mestre', 'FIRE', 'MASTER', 105, 115, 100, 'EPIC'),
    (gen_random_uuid(), NULL, 'Azula Adulto', 'FIRE', 'ADULT', 115, 95, 85, 'EPIC'),
    (gen_random_uuid(), NULL, 'Korra Adulto', 'AVATAR', 'ADULT', 110, 105, 95, 'EPIC'),
    (gen_random_uuid(), NULL, 'Zaheer', 'AIR', 'ADULT', 105, 95, 85, 'EPIC'),
    (gen_random_uuid(), NULL, 'Hama', 'BLOOD', 'ADULT', 100, 90, 80, 'EPIC'),
    (gen_random_uuid(), NULL, 'Bumi', 'EARTH', 'MASTER', 110, 110, 100, 'EPIC'),

-- LEGENDARIES (100-120% mais fortes que COMMON)
    (gen_random_uuid(), NULL, 'Aang Avatar Mestre', 'AVATAR', 'MASTER', 150, 150, 130, 'LEGENDARY'),
    (gen_random_uuid(), NULL, 'Korra Avatar Mestre', 'AVATAR', 'MASTER', 155, 145, 135, 'LEGENDARY'),
    (gen_random_uuid(), NULL, 'Roku', 'AVATAR', 'MASTER', 160, 160, 140, 'LEGENDARY'),
    (gen_random_uuid(), NULL, 'Kyoshi', 'AVATAR', 'MASTER', 170, 165, 150, 'LEGENDARY'),
    (gen_random_uuid(), NULL, 'Wan', 'AVATAR', 'MASTER', 165, 155, 145, 'LEGENDARY');