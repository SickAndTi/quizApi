CREATE TABLE IF NOT EXISTS quiz_exam_inapp_purchase
  (
     id                 BIGSERIAL NOT NULL,
     transaction_id     INT8 NOT NULL,
     sku_id             VARCHAR(255),
     purchase_time      TEXT,
     purchase_token     VARCHAR(255),
     order_id           VARCHAR(255),
     PRIMARY KEY (id)
  ) ;