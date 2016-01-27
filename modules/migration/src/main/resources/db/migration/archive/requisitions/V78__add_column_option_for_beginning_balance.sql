UPDATE master_rnr_columns
set calculationOption = '[{"name":"Default", "id":"DEFAULT"},{"name":"Allow users to override beginning balance","id":"ALLOW_USER_OVERRIDE"}]'
where name = 'beginningBalance';