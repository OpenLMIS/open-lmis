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
(1, (select id from programs where code = 'TB'),  true, 'R', 1,  'Product Code'),
(2, (select id from programs where code = 'TB'),  true, 'R', 2,  'Product'),
(4, (select id from programs where code = 'TB'),  true, 'U', 3,  'Beginning Balance'),
(5, (select id from programs where code = 'TB'),  true, 'U', 4,  'Total Received Quantity'),
(7, (select id from programs where code = 'TB'),  true, 'U', 5,  'Total Consumed Quantity'),
(9, (select id from programs where code = 'TB'),  true, 'C', 6,  'Stock on Hand'),
(11, (select id from programs where code = 'TB'), true, 'U', 7, 'Total Stockout Days'),
(15, (select id from programs where code = 'TB'), true, 'C', 8, 'Calculated Order Quantity'),
(16, (select id from programs where code = 'TB'), true, 'U', 9, 'Requested Quantity'),
(17, (select id from programs where code = 'TB'), true, 'U', 10, 'Requested Quantity Explanation'),
(1, (select id from programs where code = 'MALARIA'),  true, 'R', 1,  'Product Code'),
(2, (select id from programs where code = 'MALARIA'),  true, 'R', 2,  'Product'),
(3, (select id from programs where code = 'MALARIA'),  true, 'R', 3,  'Unit/Unit of Issue'),
(4, (select id from programs where code = 'MALARIA'),  true, 'U', 4,  'Beginning Balance'),
(5, (select id from programs where code = 'MALARIA'),  true, 'U', 5,  'Total Received Quantity'),
(6, (select id from programs where code = 'MALARIA'),  true, 'C', 6,  'Total'),
(7, (select id from programs where code = 'MALARIA'),  true, 'U', 7,  'Total Consumed Quantity'),
(8, (select id from programs where code = 'MALARIA'),  true, 'U', 8,  'Total Losses / Adjustments'),
(9, (select id from programs where code = 'MALARIA'),  true, 'C', 9,  'Stock on Hand'),
(10, (select id from programs where code = 'MALARIA'),  true, 'U', 10, 'New Patients'),
(11, (select id from programs where code = 'MALARIA'), true, 'U', 11, 'Total Stockout Days'),
(12, (select id from programs where code = 'MALARIA'), true, 'C', 12, 'Adjusted Total Consumption'),
(13, (select id from programs where code = 'MALARIA'), true, 'C', 13, 'Average Monthly Consumption(AMC)'),
(14, (select id from programs where code = 'MALARIA'), true, 'C', 14, 'Maximum Stock Quantity'),
(15, (select id from programs where code = 'MALARIA'), true, 'C', 15, 'Calculated Order Quantity'),
(16, (select id from programs where code = 'MALARIA'), true, 'U', 16, 'Requested Quantity'),
(17, (select id from programs where code = 'MALARIA'), true, 'U', 17, 'Requested Quantity Explanation'),
(18, (select id from programs where code = 'MALARIA'), true, 'U', 18, 'Approved Quantity'),
(19, (select id from programs where code = 'MALARIA'), true, 'C', 19, 'Packs to Ship'),
(20, (select id from programs where code = 'MALARIA'), true, 'R', 20, 'Price per Pack'),
(21, (select id from programs where code = 'MALARIA'), true, 'C', 21, 'Total Cost'),
(22, (select id from programs where code = 'MALARIA'), true, 'U', 22, 'Expiration Date(MM/YYYY)'),
(23, (select id from programs where code = 'MALARIA'), true, 'U', 23, 'Remarks'),
(1, (select id from programs where code = 'ESS_MEDS'),  true, 'R', 1,  'Product Code'),
(2, (select id from programs where code = 'ESS_MEDS'),  true, 'R', 2,  'Product'),
(3, (select id from programs where code = 'ESS_MEDS'),  true, 'R', 3,  'Unit/Unit of Issue'),
(4, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 4,  'Beginning Balance'),
(5, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 5,  'Total Received Quantity'),
(6, (select id from programs where code = 'ESS_MEDS'),  true, 'C', 6,  'Total'),
(7, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 7,  'Total Consumed Quantity'),
(8, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 8,  'Total Losses / Adjustments'),
(9, (select id from programs where code = 'ESS_MEDS'),  true, 'C', 9,  'Stock on Hand'),
(10, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 10, 'New Patients'),
(11, (select id from programs where code = 'ESS_MEDS'), true, 'U', 11, 'Total Stockout Days'),
(12, (select id from programs where code = 'ESS_MEDS'), true, 'C', 12, 'Adjusted Total Consumption'),
(13, (select id from programs where code = 'ESS_MEDS'), true, 'C', 13, 'Average Monthly Consumption(AMC)'),
(14, (select id from programs where code = 'ESS_MEDS'), true, 'C', 14, 'Maximum Stock Quantity'),
(15, (select id from programs where code = 'ESS_MEDS'), true, 'C', 15, 'Calculated Order Quantity'),
(16, (select id from programs where code = 'ESS_MEDS'), true, 'U', 16, 'Requested Quantity'),
(17, (select id from programs where code = 'ESS_MEDS'), true, 'U', 17, 'Requested Quantity Explanation'),
(18, (select id from programs where code = 'ESS_MEDS'), true, 'U', 18, 'Approved Quantity'),
(19, (select id from programs where code = 'ESS_MEDS'), true, 'C', 19, 'Packs to Ship'),
(20, (select id from programs where code = 'ESS_MEDS'), true, 'R', 20, 'Price per Pack'),
(21, (select id from programs where code = 'ESS_MEDS'), true, 'C', 21, 'Total Cost'),
(22, (select id from programs where code = 'ESS_MEDS'), true, 'U', 22, 'Expiration Date(MM/YYYY)'),
(23, (select id from programs where code = 'ESS_MEDS'), true, 'U', 23, 'Remarks');


update programs set templateConfigured = true where id in ((select id from programs where code = 'ESS_MEDS'),
(select id from programs where code = 'TB'), (select id from programs where code = 'MALARIA'));
