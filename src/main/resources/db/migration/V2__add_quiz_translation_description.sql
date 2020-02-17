ALTER TABLE quiz_translations ADD COLUMN IF NOT EXISTS description TEXT;
UPDATE quiz_translations SET description = '' WHERE description=NULL;