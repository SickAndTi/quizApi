alter table quiz_transaction ADD COLUMN IF NOT EXISTS user_id INT8;

ALTER TABLE quiz_transaction ADD CONSTRAINT fk_quiz_transaction_to_user FOREIGN KEY (user_id) REFERENCES users(id);