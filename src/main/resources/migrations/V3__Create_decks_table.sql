CREATE TABLE decks (
   id VARCHAR(36) PRIMARY KEY,
   name VARCHAR(100) NOT NULL,
   user_id VARCHAR(36) NOT NULL,
   card1_id VARCHAR(36) NOT NULL,
   card2_id VARCHAR(36) NOT NULL,
   card3_id VARCHAR(36) NOT NULL,
   card4_id VARCHAR(36) NOT NULL,
   card5_id VARCHAR(36) NOT NULL,
   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
   FOREIGN KEY (card1_id) REFERENCES cards(id),
   FOREIGN KEY (card2_id) REFERENCES cards(id),
   FOREIGN KEY (card3_id) REFERENCES cards(id),
   FOREIGN KEY (card4_id) REFERENCES cards(id),
   FOREIGN KEY (card5_id) REFERENCES cards(id),
   CONSTRAINT chk_unique_cards CHECK (
       card1_id != card2_id AND
       card1_id != card3_id AND
       card1_id != card4_id AND
       card1_id != card5_id AND
       card2_id != card3_id AND
       card2_id != card4_id AND
       card2_id != card5_id AND
       card3_id != card4_id AND
       card3_id != card5_id AND
       card4_id != card5_id
    )
);

CREATE INDEX idx_decks_user_id ON decks(user_id);
CREATE UNIQUE INDEX unique_deck_cards ON decks(card1_id, card2_id, card3_id, card4_id, card5_id);
