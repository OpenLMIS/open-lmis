--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

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

