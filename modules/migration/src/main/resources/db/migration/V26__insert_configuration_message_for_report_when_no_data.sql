delete from configurations where key = 'REPORT_MESSAGE_WHEN_NO_DATA';
INSERT INTO configurations(key, value)
   VALUES ('REPORT_MESSAGE_WHEN_NO_DATA', 'no data found');

