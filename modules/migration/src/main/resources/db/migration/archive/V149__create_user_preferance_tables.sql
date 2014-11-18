CREATE TABLE user_preference_master
(
  id                        SERIAL PRIMARY KEY,
  key                       VARCHAR(50) NOT NULL UNIQUE ,
  name                      VARCHAR(50) NOT NULL,
  groupName                 VARCHAR(50),
  groupDisplayOrder         INT DEFAULT 1,
  displayOrder              INT,
  description               VARCHAR(2000),
  entityType                VARCHAR(50),
  inputType                 VARCHAR(50),
  dataType                  VARCHAR(50),
  defaultValue              VARCHAR(2000),
  isActive                  BOOLEAN DEFAULT TRUE,

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_preference_roles
(
  roleId                    INT NOT NULL REFERENCES roles (id),
  userPreferenceKey         VARCHAR(50) REFERENCES user_preference_master(key),
  isApplicable              BOOLEAN,
  defaultValue              VARCHAR (2000),

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_preferences
(
  userId                    INT NOT NULL REFERENCES users(id),
  userPreferenceKey         VARCHAR(50) REFERENCES user_preference_master(key),
  value                     VARCHAR (2000),

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



