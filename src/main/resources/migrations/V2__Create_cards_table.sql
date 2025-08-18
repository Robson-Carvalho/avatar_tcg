CREATE TYPE element_type AS ENUM ('WATER','FIRE','EARTH','AIR','BLOOD','METAL','LIGHTNING','AVATAR');
CREATE TYPE phase_type AS ENUM ('YOUNG','ADULT','MASTER');
CREATE TYPE rarity_type AS ENUM ('COMMON','RARE','EPIC','LEGENDARY');

CREATE TABLE cards (
   id VARCHAR(36) PRIMARY KEY,
   user_id VARCHAR(36),
   name VARCHAR(100) NOT NULL,
   element element_type NOT NULL,
   phase phase_type NOT NULL,
   attack INT NOT NULL,
   defense INT NOT NULL,
   rarity rarity_type NOT NULL,
   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
