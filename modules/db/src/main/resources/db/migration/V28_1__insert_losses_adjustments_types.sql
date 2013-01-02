INSERT INTO losses_adjustments_types (name, description, additive, displayOrder) VALUES
('TRANSFER_IN','Transfer In'	,TRUE	,2),
('TRANSFER_OUT', 'Transfer Out'	,FALSE,	3),
('DAMAGED', 'Damaged',	FALSE	,1),
('LOST', 'Lost',FALSE,	7),
('STOLEN', 'Stolen'	,FALSE,	8),
('EXPIRED'	, 'Expired', FALSE	,4),
('PASSED_OPEN_VIAL_TIME_LIMIT',	'Passed Open-Vial Time Limit', FALSE	,5),
('COLD_CHAIN_FAILURE','Cold Chain Failure',	 FALSE,	6),
('CLINIC_RETURN', 'Clinic Return',	TRUE,	9);

