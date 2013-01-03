INSERT INTO processing_schedules(code, name, description) values('Q1stM', 'QuarterMonthly', 'QuarterMonth');
INSERT INTO processing_schedules(code, name, description) values('M', 'Monthly', 'Month');


INSERT INTO processing_periods
(name, description, startDate, endDate, scheduleId, modifiedBy) VALUES
('Period1', 'first period',  '2012-12-01', '2013-01-15', (SELECT id FROM processing_schedules LIMIT 1), (SELECT id FROM users LIMIT 1)),
('Period2', 'second period', '2013-01-16', '2013-04-30', (SELECT id FROM processing_schedules LIMIT 1), (SELECT id FROM users LIMIT 1));