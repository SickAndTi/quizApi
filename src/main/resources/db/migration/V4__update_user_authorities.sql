UPDATE authorities
SET authority='ADMIN'
WHERE authorities.authority='ROLE_ADMIN';

UPDATE authorities
SET authority='USER'
WHERE authorities.authority='ROLE_USER';