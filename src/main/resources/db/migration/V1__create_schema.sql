CREATE TABLE IF NOT EXISTS authorities
  (
     authority VARCHAR(255) NOT NULL,
     user_id   INT8 NOT NULL,
     created   TIMESTAMP,
     updated   TIMESTAMP,
     PRIMARY KEY (authority, user_id)
  ) ;

CREATE TABLE IF NOT EXISTS oauth_access_token
  (
     token_id          VARCHAR(255) NOT NULL,
     authentication    BYTEA,
     authentication_id VARCHAR(255),
     client_id         VARCHAR(255),
     created           TIMESTAMP,
     refresh_token     VARCHAR(255),
     token             BYTEA,
     updated           TIMESTAMP,
     user_name         VARCHAR(255),
     PRIMARY KEY (token_id)
  ) ;

CREATE TABLE IF NOT EXISTS oauth_client_details
  (
     client_id               VARCHAR(255) NOT NULL,
     access_token_validity   INT4 NOT NULL,
     additional_information  VARCHAR(255),
     authorities             VARCHAR(255),
     authorized_grant_types  VARCHAR(255),
     autoapprove             VARCHAR(255),
     client_secret           VARCHAR(255),
     created                 TIMESTAMP,
     refresh_token_validity  INT4 NOT NULL,
     resource_ids            VARCHAR(255),
     scope                   VARCHAR(255),
     updated                 TIMESTAMP,
     web_server_redirect_uri VARCHAR(255),
     PRIMARY KEY (client_id)
  ) ;

CREATE TABLE IF NOT EXISTS oauth_client_token
  (
     token_id          VARCHAR(255) NOT NULL,
     authentication_id VARCHAR(255),
     client_id         VARCHAR(255),
     created           TIMESTAMP,
     token             BYTEA,
     updated           TIMESTAMP,
     user_name         VARCHAR(255),
     PRIMARY KEY (token_id)
  ) ;

CREATE TABLE IF NOT EXISTS oauth_refresh_token
  (
     token_id       VARCHAR(255) NOT NULL,
     authentication BYTEA,
     created        TIMESTAMP,
     token          BYTEA,
     updated        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     PRIMARY KEY (token_id)
  ) ;

CREATE TABLE IF NOT EXISTS quiz
  (
     id          BIGSERIAL NOT NULL,
     approved    BOOLEAN NOT NULL,
     approver_id INT8,
     author_id   INT8,
     created     TIMESTAMP,
     image_url   VARCHAR(255),
     scp_number  VARCHAR(255),
     updated     TIMESTAMP,
     PRIMARY KEY (id)
  ) ;

CREATE TABLE IF NOT EXISTS quiz_translation_phrases
  (
     id                  BIGSERIAL NOT NULL,
     approved            BOOLEAN NOT NULL,
     approver_id         INT8,
     author_id           INT8,
     created             TIMESTAMP,
     translation         VARCHAR(255),
     updated             TIMESTAMP,
     quiz_translation_id INT8,
     quiz_translation    INT8,
     PRIMARY KEY (id)
  ) ;

CREATE TABLE IF NOT EXISTS quiz_translations
  (
     id          BIGSERIAL NOT NULL,
     approved    BOOLEAN NOT NULL,
     approver_id INT8,
     author_id   INT8,
     created     TIMESTAMP,
     description VARCHAR(255),
     lang_code   VARCHAR(255),
     translation VARCHAR(255),
     updated     TIMESTAMP,
     quiz_id     INT8,
     quiz        INT8,
     PRIMARY KEY (id)
  ) ;

CREATE TABLE IF NOT EXISTS users
  (
     id          BIGSERIAL NOT NULL,
     avatar      VARCHAR(255),
     created     TIMESTAMP,
     enabled     BOOLEAN NOT NULL,
     facebook_id VARCHAR(255),
     full_name   VARCHAR(255),
     google_id   VARCHAR(255),
     password    VARCHAR(255),
     username    VARCHAR(255),
     name_first  VARCHAR(255),
     name_second VARCHAR(255),
     name_third  VARCHAR(255),
     updated     TIMESTAMP,
     vk_id       VARCHAR(255),
     PRIMARY KEY (id)
  ) ;


ALTER TABLE users DROP CONSTRAINT IF EXISTS UK_r43af9ap4edm43mmtq01oddj6;
alter table users add constraint UK_r43af9ap4edm43mmtq01oddj6 unique (username);

ALTER TABLE authorities DROP CONSTRAINT IF EXISTS fkk91upmbueyim93v469wj7b2qh;
ALTER TABLE authorities
  ADD CONSTRAINT fkk91upmbueyim93v469wj7b2qh FOREIGN KEY (user_id) REFERENCES
  users ;

ALTER TABLE quiz_translation_phrases DROP CONSTRAINT IF EXISTS fks54ynq12u2pm8f70c9vucu1yu;
ALTER TABLE quiz_translation_phrases
  ADD CONSTRAINT fks54ynq12u2pm8f70c9vucu1yu FOREIGN KEY (quiz_translation_id)
  REFERENCES quiz_translations ;

ALTER TABLE quiz_translation_phrases DROP CONSTRAINT IF EXISTS fkaevrc63m2qo5rdg59h77tds98;
ALTER TABLE quiz_translation_phrases
  ADD CONSTRAINT fkaevrc63m2qo5rdg59h77tds98 FOREIGN KEY (quiz_translation)
  REFERENCES quiz_translations ;

ALTER TABLE quiz_translations DROP CONSTRAINT IF EXISTS fkhw4iq354tew23n8onopmkroma;
ALTER TABLE quiz_translations
  ADD CONSTRAINT fkhw4iq354tew23n8onopmkroma FOREIGN KEY (quiz_id) REFERENCES
  quiz ;

ALTER TABLE quiz_translations DROP CONSTRAINT IF EXISTS fkp4d0uh4fqsv7bqx47msxjce90;
ALTER TABLE quiz_translations
  ADD CONSTRAINT fkp4d0uh4fqsv7bqx47msxjce90 FOREIGN KEY (quiz) REFERENCES quiz ;

INSERT INTO users
            (name_first,
             name_second,
             name_third,
             username,
             password,
             avatar,
             enabled)
VALUES     ('Ivan',
            'Ivanov',
            'Ivanovich',
            'test@test.ru',
            '$2a$10$eWYCXHSkfoBS8Yy2Tx3LuufevEs8bJZtAtmQSjIJScDSNQ9ORBY5C',
            'https://avatars0.githubusercontent.com/u/9077598',
            'true') ON CONFLICT DO NOTHING;

INSERT INTO authorities
            (user_id,
             authority)
VALUES     (1,
            'ADMIN') ON CONFLICT DO NOTHING;

INSERT INTO authorities
            (user_id,
             authority)
VALUES     (1,
            'USER') ON CONFLICT DO NOTHING;

INSERT INTO oauth_client_details
            (client_id,
             resource_ids,
             client_secret,
             scope,
             authorized_grant_types,
             web_server_redirect_uri,
             authorities,
             access_token_validity,
             refresh_token_validity,
             additional_information,
             autoapprove)
VALUES     ('client_id',
            '',
            '$2a$10$Wp6PCClI2K4Nixlll9vs7.r.RAEljVe7Zr15gUxRRGZ31rO84lWDm',
            'read,write',
            'client_credentials,password,refresh_token',
            '',
            'ADMIN,USER',
            3600,
            0,
            '',
            'true') ON CONFLICT DO NOTHING;