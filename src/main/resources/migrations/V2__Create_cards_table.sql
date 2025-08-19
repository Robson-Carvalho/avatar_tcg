

CREATE TABLE cards (
   id VARCHAR(36) PRIMARY KEY,
   user_id VARCHAR(36),
   name VARCHAR(100) NOT NULL,
   element VARCHAR(100) NOT NULL,
   rarity VARCHAR(100) NOT NULL,
   phase VARCHAR(100) NOT NULL,
   attack INT NOT NULL,
   life INT NOT NULL,
   defense INT NOT NULL,
   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
