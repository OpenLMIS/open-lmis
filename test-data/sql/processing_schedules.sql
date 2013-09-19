-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

INSERT INTO processing_schedules (code, name, description) VALUES ('Q1stM', 'QuarterMonthly', 'QuarterMonth');
INSERT INTO processing_schedules (code, name, description) VALUES ('M', 'Monthly', 'Month');


INSERT INTO processing_periods
(name, description, startDate, endDate, numberOfMonths, scheduleId, modifiedBy) VALUES
('Period1', 'first period', '2012-12-01', '2012-01-15 23:59:59', 2, (SELECT
                                                                       id
                                                                     FROM processing_schedules
                                                                     WHERE code = 'Q1stM'), (SELECT
                                                                                               id
                                                                                             FROM users
                                                                                             LIMIT 1)),
('Period2', 'second period', '2012-02-01', '2012-03-28 23:59:59', 2, (SELECT
                                                                        id
                                                                      FROM processing_schedules
                                                                      WHERE code = 'Q1stM'), (SELECT
                                                                                                id
                                                                                              FROM users
                                                                                              LIMIT 1)),
('Dec2012', 'Dec2012', '2012-12-01', '2012-12-31 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('Jan2013', 'Jan2013', '2013-01-01', '2013-01-31 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('Feb2013', 'Feb2013', '2013-02-01', '2013-02-28 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('Mar2013', 'Mar2013', '2013-03-01', '2013-03-31 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('Apr2013', 'Apr2013', '2013-04-01', '2013-04-30 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('May2013', 'May2013', '2013-05-01', '2013-05-31 23:59:59', 1, (SELECT
                                                                  id
                                                                FROM processing_schedules
                                                                WHERE code = 'M'), (SELECT
                                                                                      id
                                                                                    FROM users
                                                                                    LIMIT 1)),
('June2013', 'June2013', '2013-06-01', '2013-06-30 23:59:59', 1, (SELECT
                                                                    id
                                                                  FROM processing_schedules
                                                                  WHERE code = 'M'), (SELECT
                                                                                        id
                                                                                      FROM users
                                                                                      LIMIT 1));