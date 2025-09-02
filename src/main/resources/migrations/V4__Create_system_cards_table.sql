CREATE TABLE system_cards (
   id VARCHAR(36) PRIMARY KEY,
   name VARCHAR(100) NOT NULL,
   description TEXT NOT NULL,
   element VARCHAR(100) NOT NULL,
   rarity VARCHAR(100) NOT NULL,
   phase VARCHAR(100) NOT NULL,
   attack INT NOT NULL,
   life INT NOT NULL,
   defense INT NOT NULL,
   quantity INT NOT NULL
);
