CREATE TABLE sync_up_hashes(
  hash VARCHAR(128) PRIMARY KEY
);
-- we will use this table to store stock movement content hashes
-- so when the tablet tries to re-sync the same record twice due to http response IO interruption
-- we can reject the re-sync up