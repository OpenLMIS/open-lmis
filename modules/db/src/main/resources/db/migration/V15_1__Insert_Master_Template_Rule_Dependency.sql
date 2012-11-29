insert into master_template_column_rules
(column_name, dependent_column_name) values 

('quantityDispensed','beginningBalance'),
('quantityDispensed','quantityReceived'),
('quantityDispensed','lossesAndAdjustments'),
('quantityDispensed','stockInHand'),

('stockInHand','beginningBalance'),
('stockInHand','quantityReceived'),
('stockInHand','lossesAndAdjustments'),
('stockInHand','quantityDispensed'),

('stockOutDays', 'normalizedConsumption'),
('normalizedConsumption', 'stockOutDays'),

('quantityRequested', 'reasonForRequestedQuantity'),
('reasonForRequestedQuantity', 'quantityRequested');