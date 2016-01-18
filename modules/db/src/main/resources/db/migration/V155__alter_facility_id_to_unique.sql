DELETE FROM moz_app_info WHERE id IN
(SELECT a.id FROM moz_app_info a JOIN moz_app_info b on a.facilityid = b.facilityid WHERE a.id<b.id);

ALTER TABLE moz_app_info ADD CONSTRAINT moz_app_info_facilityId_key UNIQUE (facilityId)