ALTER TABLE vaccine_report_logistics_line_items
ADD daysStockedout INTEGER NULL;

ALTER TABLE vaccine_report_logistics_line_items
ADD reasonForDiscardingUnopened VARCHAR (500) NULL;

ALTER TABLE vaccine_report_logistics_line_items
ADD remarks VARCHAR(500) NULL;