--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

INSERT INTO processing_schedules (code, name, description) VALUES ('Q1stM', 'QuarterMonthly', 'QuarterMonth');
INSERT INTO processing_schedules (code, name, description) VALUES ('M', 'Monthly', 'Month');


INSERT INTO processing_periods
(name, description, startDate, endDate, numberOfMonths, scheduleId, modifiedBy) VALUES
('Sept2013', 'Sept2013', '2013-09-01', '2013-09-30 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1)),
('Oct2013', 'Oct2013', '2013-10-01', '2013-10-31 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1)),
('Nov2013', 'Nov2013', '2013-11-01', '2013-11-30 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1)),
('Dec2013', 'Dec2013', '2013-12-01', '2013-12-31 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1)),
('Jan2014', 'Jan2014', '2014-01-01', '2014-01-31 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1)),
('Feb2014', 'Feb2014', '2014-02-01', '2014-02-28 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1)),
('Mar2014', 'Mar2014', '2014-03-01', '2014-03-31 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1)),
('Apr2014', 'Apr2014', '2014-04-01', '2014-04-30 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1)),
('May2014', 'May2014', '2014-05-01', '2014-05-31 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1)),
('June2014', 'June2014', '2014-06-01', '2014-06-30 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1)),
('July2014', 'July2014', '2014-07-01', '2014-07-31 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1)),
('Aug2014', 'Aug2014', '2014-08-01', '2014-08-31 23:59:59', 1, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1));