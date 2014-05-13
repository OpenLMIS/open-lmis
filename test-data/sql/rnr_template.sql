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
(masterColumnId, rnrOptionId, programId, visible, source, position, label)
values
(1, null , (select id from programs where code = 'TB'), true,'U',   1,  'Skip'),
(2, null , (select id from programs where code = 'TB'), true, 'R',  2,  'Product Code'),
(3, null , (select id from programs where code = 'TB'), true, 'R',  3,  'Product'),
(4, null , (select id from programs where code = 'TB'), false, 'R', 4,  'Unit/Unit of Issue'),
(5, null , (select id from programs where code = 'TB'), true, 'U',  5,  'Beginning Balance'),
(6, null , (select id from programs where code = 'TB'), true, 'U',  6,  'Total Received Quantity'),
(7, null , (select id from programs where code = 'TB'), false, 'C', 7,  'Total'),
(8, null , (select id from programs where code = 'TB'), true, 'U',  8,  'Total Consumed Quantity'),
(9, null , (select id from programs where code = 'TB'), false, 'U', 9,  'Total Losses / Adjustments'),
(10, null , (select id from programs where code = 'TB'), true, 'C',  10,  'Stock on Hand'),
(11, 1    , (select id from programs where code = 'TB'), false, 'U', 11, 'New Patients'),
(12, null , (select id from programs where code = 'TB'), true, 'U',  12, 'Total Stockout Days'),
(13, null , (select id from programs where code = 'TB'), false, 'C', 13, 'Monthly Normalized Consumption'),
(25, null , (select id from programs where code = 'TB'), false, 'C', 14, 'Period Normalized Consumption'),
(14, null , (select id from programs where code = 'TB'), false, 'C', 15, 'Average Monthly Consumption(AMC)'),
(15, null , (select id from programs where code = 'TB'), false, 'C', 16, 'Maximum Stock Quantity'),
(16, null , (select id from programs where code = 'TB'), true, 'C',  17, 'Calculated Order Quantity'),
(17, null , (select id from programs where code = 'TB'), true, 'U',  18, 'Requested Quantity'),
(18, null , (select id from programs where code = 'TB'), true, 'U',  19, 'Requested Quantity Explanation'),
(19, null , (select id from programs where code = 'TB'), false, 'U', 20, 'Approved Quantity'),
(20, null , (select id from programs where code = 'TB'), false, 'C', 21, 'Packs to Ship'),
(21, null , (select id from programs where code = 'TB'), false, 'R', 22, 'Price per Pack'),
(22, null , (select id from programs where code = 'TB'), false, 'C', 23, 'Total Cost'),
(23, null , (select id from programs where code = 'TB'), false, 'U', 24, 'Expiration Date(MM/YYYY)'),
(24, null , (select id from programs where code = 'TB'), false, 'U', 25, 'Remarks'),

(1, null, (select id from programs where code = 'MALARIA'),  true,'U', 1,  'Skip'),
(2, null, (select id from programs where code = 'MALARIA'),  true, 'R', 2,  'Product Code'),
(3, null, (select id from programs where code = 'MALARIA'),  true, 'R', 3,  'Product'),
(4, null, (select id from programs where code = 'MALARIA'),  true, 'R', 4,  'Unit/Unit of Issue'),
(5, null, (select id from programs where code = 'MALARIA'),  true, 'U', 5,  'Beginning Balance'),
(6, null, (select id from programs where code = 'MALARIA'),  true, 'U', 6,  'Total Received Quantity'),
(7, null, (select id from programs where code = 'MALARIA'),  true, 'C', 7,  'Total'),
(8, null, (select id from programs where code = 'MALARIA'),  true, 'U', 8,  'Total Consumed Quantity'),
(9, null, (select id from programs where code = 'MALARIA'),  true, 'U', 9,  'Total Losses / Adjustments'),
(10, null, (select id from programs where code = 'MALARIA'),  true, 'C', 10,  'Stock on Hand'),
(11, 1   , (select id from programs where code = 'MALARIA'),  true, 'U', 11, 'New Patients'),
(12, null, (select id from programs where code = 'MALARIA'), true, 'U', 12, 'Total Stockout Days'),
(13, null, (select id from programs where code = 'MALARIA'), true, 'C', 13, 'Monthly Normalized Consumption'),
(25, null, (select id from programs where code = 'MALARIA'), true, 'C', 14, 'Period Normalized Consumption'),
(14, null, (select id from programs where code = 'MALARIA'), true, 'C', 15, 'Average Monthly Consumption(AMC)'),
(15, null, (select id from programs where code = 'MALARIA'), true, 'C', 16, 'Maximum Stock Quantity'),
(16, null, (select id from programs where code = 'MALARIA'), true, 'C', 17, 'Calculated Order Quantity'),
(17, null, (select id from programs where code = 'MALARIA'), true, 'U', 18, 'Requested Quantity'),
(18, null, (select id from programs where code = 'MALARIA'), true, 'U', 19, 'Requested Quantity Explanation'),
(19, null, (select id from programs where code = 'MALARIA'), true, 'U', 20, 'Approved Quantity'),
(20, null, (select id from programs where code = 'MALARIA'), true, 'C', 21, 'Packs to Ship'),
(21, null, (select id from programs where code = 'MALARIA'), true, 'R', 22, 'Price per Pack'),
(22, null, (select id from programs where code = 'MALARIA'), true, 'C', 23, 'Total Cost'),
(23, null, (select id from programs where code = 'MALARIA'), true, 'U', 24, 'Expiration Date(MM/YYYY)'),
(24, null, (select id from programs where code = 'MALARIA'), true, 'U', 25, 'Remarks'),

(1, null , (select id from programs where code = 'ESS_MEDS'),  true,'U', 1,  'Skip'),
(2, null , (select id from programs where code = 'ESS_MEDS'),  true, 'R', 2,  'Product Code'),
(3, null , (select id from programs where code = 'ESS_MEDS'),  true, 'R', 3,  'Product'),
(4, null , (select id from programs where code = 'ESS_MEDS'),  true, 'R', 4,  'Unit/Unit of Issue'),
(5, null , (select id from programs where code = 'ESS_MEDS'),  true, 'U', 5,  'Beginning Balance'),
(6, null , (select id from programs where code = 'ESS_MEDS'),  true, 'U', 6,  'Total Received Quantity'),
(7, null , (select id from programs where code = 'ESS_MEDS'),  true, 'C', 7,  'Total'),
(8, null , (select id from programs where code = 'ESS_MEDS'),  true, 'U', 8,  'Total Consumed Quantity'),
(9, null , (select id from programs where code = 'ESS_MEDS'),  true, 'U', 9,  'Total Losses / Adjustments'),
(10, null , (select id from programs where code = 'ESS_MEDS'),  true, 'C', 10,  'Stock on Hand'),
(11, 1    , (select id from programs where code = 'ESS_MEDS'),  true, 'U', 11, 'New Patients'),
(12, null , (select id from programs where code = 'ESS_MEDS'), true, 'U', 12, 'Total Stockout Days'),
(13, null , (select id from programs where code = 'ESS_MEDS'), true, 'C', 13, 'Monthly Normalized Consumption'),
(25, null , (select id from programs where code = 'ESS_MEDS'), true, 'C', 14, 'Period Normalized Consumption'),
(14, null , (select id from programs where code = 'ESS_MEDS'), true, 'C', 15, 'Average Monthly Consumption(AMC)'),
(15, null , (select id from programs where code = 'ESS_MEDS'), true, 'C', 16, 'Maximum Stock Quantity'),
(16, null , (select id from programs where code = 'ESS_MEDS'), true, 'C', 17, 'Calculated Order Quantity'),
(17, null , (select id from programs where code = 'ESS_MEDS'), true, 'U', 18, 'Requested Quantity'),
(18, null , (select id from programs where code = 'ESS_MEDS'), true, 'U', 19, 'Requested Quantity Explanation'),
(19, null , (select id from programs where code = 'ESS_MEDS'), true, 'U', 20, 'Approved Quantity'),
(20, null , (select id from programs where code = 'ESS_MEDS'), true, 'C', 21, 'Packs to Ship'),
(21, null , (select id from programs where code = 'ESS_MEDS'), true, 'R', 22, 'Price per Pack'),
(22, null , (select id from programs where code = 'ESS_MEDS'), true, 'C', 23, 'Total Cost'),
(23, null , (select id from programs where code = 'ESS_MEDS'), true, 'U', 24, 'Expiration Date(MM/YYYY)'),
(24, null , (select id from programs where code = 'ESS_MEDS'), true, 'U', 25, 'Remarks');


update programs set templateConfigured = true where id in ((select id from programs where code = 'ESS_MEDS'),
(select id from programs where code = 'TB'), (select id from programs where code = 'MALARIA'));
