delete from user_preferences where userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE';
delete from user_preference_master where key = 'DEFAULT_GEOGRAPHIC_ZONE';
INSERT INTO "user_preference_master" VALUES (11, 'DEFAULT_GEOGRAPHIC_ZONE', 'Default Geographic Zone', 'DEFAULTS', 1, 1, 'Set default', 'geographiczone', 'single-selected', 'int', '1', 't', NULL, '2014-6-26 10:00:09.213', NULL, '2014-6-26 10:00:09.213');
INSERT INTO "user_preferences" VALUES (2, 'DEFAULT_GEOGRAPHIC_ZONE', '437', NULL, '2014-4-2 13:42:06.174776', NULL, '2014-4-2 13:42:06.174776');


--update user preference for facility and geozone
DELETE FROM user_preferences where userid = 2 and userpreferencekey = 'DEFAULT_FACILITY';
INSERT INTO user_preferences(userid, userpreferencekey, value, createdby, createddate, modifiedby,
            modifieddate)
    VALUES (2,'DEFAULT_FACILITY','16406',NULL,'2014-04-02 13:42:06.174776',NULL,'2014-04-02 13:42:06.174776');

DELETE FROM user_preferences where userid = 2 and userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE';

INSERT INTO user_preferences(userid, userpreferencekey, value, createdby, createddate, modifiedby,
            modifieddate)
    VALUES (2,'DEFAULT_GEOGRAPHIC_ZONE','437',NULL,'2014-04-02 13:42:06.174776',NULL,'2014-04-02 13:42:06.174776');


