CREATE TABLE user_password_reset_tokens (
  userId INT NOT NULL REFERENCES users(id),
  token VARCHAR(250) NOT NULL,
  UNIQUE (userId, token)
);
