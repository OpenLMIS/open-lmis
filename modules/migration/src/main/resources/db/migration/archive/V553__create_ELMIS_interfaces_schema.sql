-- Table: interface_apps
CREATE TABLE interface_apps
(
  id                SERIAL PRIMARY KEY                               ,
  name              VARCHAR (100)                            NOT NULL,
  active            BOOLEAN                              DEFAULT TRUE,
  createdBy         INTEGER                                          ,
  createdDate       TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                          ,
  modifiedDate      TIMESTAMP                DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE interface_apps IS                     'Applications with which eLMIS interfaces';
COMMENT ON COLUMN interface_apps.id IS                 'ID';
COMMENT ON COLUMN interface_apps.active IS             'Active';
COMMENT ON COLUMN interface_apps.createdBy IS          'Created by';
COMMENT ON COLUMN interface_apps.createdDate IS        'Created on';
COMMENT ON COLUMN interface_apps.modifiedBy IS         'Modified by';
COMMENT ON COLUMN interface_apps.modifiedDate IS       'Modified on';

-- Table: interface_dataset
CREATE TABLE interface_dataset
(
  id                SERIAL PRIMARY KEY                               ,
  interfaceId       INTEGER REFERENCES interface_apps   (id) NOT NULL,
  datasetname       VARCHAR (100)                            NOT NULL,
  datasetId         VARCHAR (60)                             NOT NULL,
  createdBy         INTEGER                                          ,
  createdDate       TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                          ,
  modifiedDate      TIMESTAMP                DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE interface_dataset IS                     'Datasets to send to interfacing apps';
COMMENT ON COLUMN interface_dataset.id IS                 'Id';
COMMENT ON COLUMN interface_dataset.interfaceId IS        'Interface Id';
COMMENT ON COLUMN interface_dataset.datasetname IS        'Dataset name';
COMMENT ON COLUMN interface_dataset.datasetId IS          'Dataset Id';
COMMENT ON COLUMN interface_dataset.createdBy IS          'Created by';
COMMENT ON COLUMN interface_dataset.createdDate IS        'Created on';
COMMENT ON COLUMN interface_dataset.modifiedBy IS         'Modified by';
COMMENT ON COLUMN interface_dataset.modifiedDate IS       'Modified on';

-- Table: facility_mappings
CREATE TABLE facility_mappings
(
  id                SERIAL PRIMARY KEY                               ,
  interfaceid       INTEGER REFERENCES interface_apps (id) NOT NULL,
  facilityId        INTEGER REFERENCES facilities       (id) NOT NULL,
  mappedId          VARCHAR (100)                            NOT NULL,
  active            BOOLEAN                              DEFAULT TRUE,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uc_facility_mappedid ON facility_mappings(mappedId);
COMMENT ON TABLE facility_mappings IS                    'Facility code mapping with other interfacing applications such as DHIS2';
COMMENT ON INDEX uc_facility_mappedid IS                 'Unique code required for mapped id';
COMMENT ON COLUMN facility_mappings.id IS                'ID';
COMMENT ON COLUMN facility_mappings.interfaceid IS       'Interfacing apps id';
COMMENT ON COLUMN facility_mappings.facilityId IS        'Facility id';
COMMENT ON COLUMN facility_mappings.mappedId IS          'Mapped id';
COMMENT ON COLUMN facility_mappings.active IS            'Active';
COMMENT ON COLUMN facility_mappings.createdBy IS         'Created by';
COMMENT ON COLUMN facility_mappings.createdDate IS       'Created on';
COMMENT ON COLUMN facility_mappings.modifiedBy IS        'Modified by';
COMMENT ON COLUMN facility_mappings.modifiedDate IS      'Modified on';
