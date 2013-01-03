INSERT INTO processing_schedules(code, name, description) values('Q1stM', 'QuarterMonthly', 'QuarterMonth');
INSERT INTO processing_schedules(code, name, description) values('M', 'Monthly', 'Month');


INSERT INTO processing_periods
(name, description, startDate, endDate, scheduleId, modifiedBy) VALUES
('Period1', 'first period', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM processing_schedules LIMIT 1), (SELECT id FROM users LIMIT 1));