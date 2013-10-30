--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

delete from program_rnr_columns;
insert into program_rnr_columns
(masterColumnId, programId, visible, source, position, label)
values
(1, (select id from programs where code = 'TB'),  false,'U', 1,  'Skip'),
(2, (select id from programs where code = 'TB'),  true, 'R', 2,  'Product Code'),
(3, (select id from programs where code = 'TB'),  true, 'R', 3,  'Product'),
(5, (select id from programs where code = 'TB'),  true, 'U', 4,  'Beginning Balance'),
(6, (select id from programs where code = 'TB'),  true, 'U', 5,  'Total Received Quantity'),
(8, (select id from programs where code = 'TB'),  true, 'U', 6,  'Total Consumed Quantity'),
(10, (select id from programs where code = 'TB'),  true, 'C', 7,  'Stock on Hand'),
(12, (select id from programs where code = 'TB'), true, 'U', 8, 'Total Stockout Days'),
(16, (select id from programs where code = 'TB'), true, 'C', 9, 'Calculated Order Quantity'),
(17, (select id from programs where code = 'TB'), true, 'U', 10, 'Requested Quantity'),
(18, (select id from programs where code = 'TB'), true, 'U', 11, 'Requested Quantity Explanation'),
(1, (select id from programs where code = 'MALARIA'),  false,'U', 1,  'Skip'),
(2, (select id from programs where code = 'MALARIA'),  true, 'R', 2,  'Product Code'),
(3, (select id from programs where code = 'MALARIA'),  true, 'R', 3,  'Product'),
(4, (select id from programs where code = 'MALARIA'),  true, 'R', 4,  'Unit/Unit of Issue'),
(5, (select id from programs where code = 'MALARIA'),  true, 'U', 5,  'Beginning Balance'),
(6, (select id from programs where code = 'MALARIA'),  true, 'U', 6,  'Total Received Quantity'),
(7, (select id from programs where code = 'MALARIA'),  true, 'C', 7,  'Total'),
(8, (select id from programs where code = 'MALARIA'),  true, 'U', 8,  'Total Consumed Quantity'),
(9, (select id from programs where code = 'MALARIA'),  true, 'U', 9,  'Total Losses / Adjustments'),
(10, (select id from programs where code = 'MALARIA'),  true, 'C', 10,  'Stock on Hand'),
(11, (select id from programs where code = 'MALARIA'),  true, 'U', 11, 'New Patients'),
(12, (select id from programs where code = 'MALARIA'), true, 'U', 12, 'Total Stockout Days'),
(13, (select id from programs where code = 'MALARIA'), true, 'C', 13, 'Adjusted Total Consumption'),
(14, (select id from programs where code = 'MALARIA'), true, 'C', 14, 'Average Monthly Consumption(AMC)'),
(15, (select id from programs where code = 'MALARIA'), true, 'C', 15, 'Maximum Stock Quantity'),
(16, (select id from programs where code = 'MALARIA'), true, 'C', 16, 'Calculated Order Quantity'),
(17, (select id from programs where code = 'MALARIA'), true, 'U', 17, 'Requested Quantity'),
(18, (select id from programs where code = 'MALARIA'), true, 'U', 18, 'Requested Quantity Explanation'),
(19, (select id from programs where code = 'MALARIA'), true, 'U', 19, 'Approved Quantity'),
(20, (select id from programs where code = 'MALARIA'), true, 'C', 20, 'Packs to Ship'),
(21, (select id from programs where code = 'MALARIA'), true, 'R', 21, 'Price per Pack'),
(22, (select id from programs where code = 'MALARIA'), true, 'C', 22, 'Total Cost'),
(23, (select id from programs where code = 'MALARIA'), true, 'U', 23, 'Expiration Date(MM/YYYY)'),
(24, (select id from programs where code = 'MALARIA'), true, 'U', 24, 'Remarks'),
(2, (select id from programs where code = 'ESS_MEDS'),  true, 'R', 2,  'Product Code'),
(3, (select id from programs where code = 'ESS_MEDS'),  true, 'R', 3,  'Product'),
(4, (select id from programs where code = 'ESS_MEDS'),  true, 'R', 4,  'Unit/Unit of Issue'),
(5, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 5,  'Beginning Balance'),
(6, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 6,  'Total Received Quantity'),
(7, (select id from programs where code = 'ESS_MEDS'),  true, 'C', 7,  'Total'),
(8, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 8,  'Total Consumed Quantity'),
(9, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 9,  'Total Losses / Adjustments'),
(10, (select id from programs where code = 'ESS_MEDS'),  true, 'C', 10,  'Stock on Hand'),
(11, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 11, 'New Patients'),
(12, (select id from programs where code = 'ESS_MEDS'), true, 'U', 12, 'Total Stockout Days'),
(13, (select id from programs where code = 'ESS_MEDS'), true, 'C', 13, 'Adjusted Total Consumption'),
(14, (select id from programs where code = 'ESS_MEDS'), true, 'C', 14, 'Average Monthly Consumption(AMC)'),
(15, (select id from programs where code = 'ESS_MEDS'), true, 'C', 15, 'Maximum Stock Quantity'),
(16, (select id from programs where code = 'ESS_MEDS'), true, 'C', 16, 'Calculated Order Quantity'),
(17, (select id from programs where code = 'ESS_MEDS'), true, 'U', 17, 'Requested Quantity'),
(18, (select id from programs where code = 'ESS_MEDS'), true, 'U', 18, 'Requested Quantity Explanation'),
(19, (select id from programs where code = 'ESS_MEDS'), true, 'U', 19, 'Approved Quantity'),
(20, (select id from programs where code = 'ESS_MEDS'), true, 'C', 20, 'Packs to Ship'),
(21, (select id from programs where code = 'ESS_MEDS'), true, 'R', 21, 'Price per Pack'),
(22, (select id from programs where code = 'ESS_MEDS'), true, 'C', 22, 'Total Cost'),
(23, (select id from programs where code = 'ESS_MEDS'), true, 'U', 23, 'Expiration Date(MM/YYYY)'),
(24, (select id from programs where code = 'ESS_MEDS'), true, 'U', 24, 'Remarks');


update programs set templateConfigured = true where id in ((select id from programs where code = 'ESS_MEDS'),
(select id from programs where code = 'TB'), (select id from programs where code = 'MALARIA'));
