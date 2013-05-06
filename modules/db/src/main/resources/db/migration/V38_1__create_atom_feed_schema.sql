-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.


CREATE TABLE atomfeed.event_records (
  id SERIAL PRIMARY KEY,
  uuid VARCHAR(40),
  title VARCHAR(255),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  uri VARCHAR(255),
  object VARCHAR(1000),
  category VARCHAR(255)
);


CREATE TABLE atomfeed.chunking_history (
  id SERIAL PRIMARY KEY,
  chunk_length BIGINT,
  start  BIGINT NOT NULL
);


INSERT INTO atomfeed.chunking_history(chunk_length, start) VALUES(5,1);
