CREATE TABLE IF NOT EXISTS quiz_transaction
  (
     id                 BIGSERIAL NOT NULL,
     quiz_id            INT8,
     transaction_type   VARCHAR(255),
     coins_amount       INT8,
     PRIMARY KEY (id)
  ) ;