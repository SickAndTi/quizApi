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
    'android_admin_client_id',
    '',
    '$2a$10$Wp6PCClI2K4Nixlll9vs7.r.RAEljVe7Zr15gUxRRGZ31rO84lWDm',
    'read,write',
    'password,refresh_token',
    '',
    'ADMIN,USER',
    604800,
    2592000,
    '',
    'true'
) ON CONFLICT DO NOTHING;