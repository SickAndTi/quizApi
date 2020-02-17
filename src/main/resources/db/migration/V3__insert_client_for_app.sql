INSERT INTO users(
    name_first,
    name_second,
    name_third,
    username,
    password,
    avatar,
    enabled
) VALUES (
    'Sick',
    'Tired',
    'And',
    'demchenko.cyrille@yandex.ru',
    '$2a$10$eWYCXHSkfoBS8Yy2Tx3LuufevEs8bJZtAtmQSjIJScDSNQ9ORBY5C',
    'https://avatars0.githubusercontent.com/u/9077598',
    'true'
) ON CONFLICT DO NOTHING;

INSERT INTO authorities(
    user_id,
    authority
) VALUES(
    (SELECT id from users WHERE username='demchenko.cyrille@yandex.ru'),
    'USER'
) ON CONFLICT DO NOTHING;

INSERT INTO authorities(
    user_id,
    authority
) VALUES(
    (SELECT id from users WHERE username='demchenko.cyrille@yandex.ru'),
    'ADMIN'
) ON CONFLICT DO NOTHING;

INSERT INTO oauth_client_details(
    client_id,
    resource_ids,
    client_secret,
    scope,
    authorized_grant_types,
    web_server_redirect_uri,
    authorities,
    access_token_validity,
    refresh_token_validity,
    additional_information,
    autoapprove
) VALUES(
    'android_app_client_id',
    '',
    '$2a$10$SqDB1CEalBxynQJSk30gIu7r12sVYCLksDYCCIep8di7FtXOq.OqC',
    'read',
    'client_credentials',
    '',
    'APP',
    604800,
    0,
    '',
    'true'
) ON CONFLICT DO NOTHING;