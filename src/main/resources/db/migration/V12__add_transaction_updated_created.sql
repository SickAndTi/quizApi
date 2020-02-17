alter table quiz_transaction ADD COLUMN IF NOT EXISTS updated TIMESTAMP;
alter table quiz_transaction ADD COLUMN IF NOT EXISTS created TIMESTAMP;