ALTER TABLE programs
ADD hideSkippedProducts BOOLEAN NOT NULL DEFAULT(false);

ALTER TABLE programs
ADD showNonFullSupplyTab BOOLEAN NOT NULL DEFAULT(true);

ALTER TABLE programs
ADD enableSkipPeriod BOOLEAN NOT NULL DEFAULT (false);


-- clean up the existing config settings
DELETE FROM configuration_settings
  WHERE key in ('ENABLE_SKIP_RNR_PERIOD','RNR_HIDE_SKIPPED_PRODUCTS', 'RNR_HIDE_NON_FULL_SUPPLY_TAB');



