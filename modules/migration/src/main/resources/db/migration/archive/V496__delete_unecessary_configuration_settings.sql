delete from configuration_settings
  where key in ('TRACK_VACCINE_OUTREACH_COVERAGE'
                  , 'VACCINE_TAB_LOGISTICS_VISIBLE'
                  , 'VACCINE_TAB_COVERAGE_VISIBLE'
                  , 'VACCINE_TAB_DISEASE_VISIBLE'
                  , 'VACCINE_TAB_AEFI_VISIBLE'
                  , 'VACCINE_TAB_TARGET_VISIBLE'
                  , 'VACCINE_TAB_COLD_CHAIN_VISIBLE'
                  , 'VACCINE_TAB_CAMPAIGN_VISIBLE'
                  , 'VACCINE_TAB_VITAMIN_SUPPLEMENTATION_VISIBLE'
  );