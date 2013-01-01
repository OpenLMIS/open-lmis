delete from program_rnr_columns;
insert into program_rnr_columns
(masterColumnId, programId, visible, source, position, label)
values
(1, (select id from programs where code = 'HIV'),  true, 'R', 1,  'Product Code'),
(2, (select id from programs where code = 'HIV'),  true, 'R', 2,  'Product'),
(3, (select id from programs where code = 'HIV'),  true, 'R', 3,  'Unit/Unit of Issue'),
(4, (select id from programs where code = 'HIV'),  true, 'U', 4,  'Beginning Balance'),
(5, (select id from programs where code = 'HIV'),  true, 'U', 5,  'Total Received Quantity'),
(6, (select id from programs where code = 'HIV'),  true, 'U', 6,  'Total Consumed Quantity'),
(7, (select id from programs where code = 'HIV'),  true, 'U', 7,  'Total Losses / Adjustments'),
(8, (select id from programs where code = 'HIV'),  true, 'U', 8,  'Reason for Losses / Adjustments'),
(9, (select id from programs where code = 'HIV'),  true, 'C', 9,  'Stock on Hand'),
(10, (select id from programs where code = 'HIV'), true, 'U', 10, 'New Patients'),
(11, (select id from programs where code = 'HIV'), true, 'U', 11, 'Total Stockout days'),
(12, (select id from programs where code = 'HIV'), true, 'C', 12, 'Adjusted Total Consumption'),
(13, (select id from programs where code = 'HIV'), true, 'C', 13, 'Average Monthly Consumption(AMC)'),
(14, (select id from programs where code = 'HIV'), true, 'C', 14, 'Maximum Stock Quantity'),
(15, (select id from programs where code = 'HIV'), true, 'C', 15, 'Calculated Order Quantity'),
(16, (select id from programs where code = 'HIV'), true, 'U', 16, 'Requested Quantity'),
(17, (select id from programs where code = 'HIV'), true, 'U', 17, 'Requested Quantity Explanation'),
(18, (select id from programs where code = 'HIV'), true, 'U', 18, 'Approved Quantity'),
(19, (select id from programs where code = 'HIV'), true, 'C', 19, 'Packs to Ship'),
(20, (select id from programs where code = 'HIV'), true, 'R', 20, 'Price per pack'),
(21, (select id from programs where code = 'HIV'), true, 'C', 21, 'Total cost'),
(22, (select id from programs where code = 'HIV'), true, 'U', 22, 'Remarks');
