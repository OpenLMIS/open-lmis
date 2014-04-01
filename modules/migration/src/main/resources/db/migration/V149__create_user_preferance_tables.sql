CREATE TABLE user_preference_master
(
  key                       VARCHAR(50) NOT NULL UNIQUE,
  name                      VARCHAR(50) NOT NULL UNIQUE,
  groupName                 VARCHAR(50),
  displayOrder              INT,
  description               VARCHAR(2000),
  entityType                VARCHAR(50),
  inputType                 VARCHAR(50),
  dataType                  VARCHAR(50),
  defaultValue              VARCHAR(2000),
  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CREATE TABLE user_role_preferences
-- (
--   roleId                    INT NOT NULL,
--   userPreferenceKey         VARCHAR(50),
--   isApplicable              BOOLEAN,
--   defaultValue              VARCHAR (2000),
--
--   createdBy                 INTEGER,
--   createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--   modifiedBy                INTEGER,
--   modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--
-- );

CREATE TABLE user_preferences
(
  userId                    INT NOT NULL,
  userPreferenceKey         VARCHAR(50),
  value                     VARCHAR (2000),

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



