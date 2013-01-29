INSERT INTO processing_schedules(code, name, description) values('Q1stM', 'QuarterMonthly', 'QuarterMonth');
INSERT INTO processing_schedules(code, name, description) values('M', 'Monthly', 'Month');


INSERT INTO processing_periods
(name, description, startDate, endDate, numberOfMonths, scheduleId, modifiedBy) VALUES
('Period1', 'first period',  '2012-12-01', '2013-01-15', 2, (SELECT id FROM processing_schedules WHERE code = 'Q1stM'), (SELECT id FROM users LIMIT 1)),
('Period2', 'second period', '2012-11-01', '2012-12-01', 3, (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1));