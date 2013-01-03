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
(8, (select id from programs where code = 'HIV'),  true, 'C', 8,  'Stock on Hand'),
(9, (select id from programs where code = 'HIV'),  true, 'U', 9, 'New Patients'),
(10, (select id from programs where code = 'HIV'), true, 'U', 10, 'Total Stockout days'),
(11, (select id from programs where code = 'HIV'), true, 'C', 11, 'Adjusted Total Consumption'),
(12, (select id from programs where code = 'HIV'), true, 'C', 12, 'Average Monthly Consumption(AMC)'),
(13, (select id from programs where code = 'HIV'), true, 'C', 13, 'Maximum Stock Quantity'),
(14, (select id from programs where code = 'HIV'), true, 'C', 14, 'Calculated Order Quantity'),
(15, (select id from programs where code = 'HIV'), true, 'U', 15, 'Requested Quantity'),
(16, (select id from programs where code = 'HIV'), true, 'U', 16, 'Requested Quantity Explanation'),
(17, (select id from programs where code = 'HIV'), true, 'U', 17, 'Approved Quantity'),
(18, (select id from programs where code = 'HIV'), true, 'C', 18, 'Packs to Ship'),
(19, (select id from programs where code = 'HIV'), true, 'R', 19, 'Price per pack'),
(20, (select id from programs where code = 'HIV'), true, 'C', 20, 'Total cost'),
(21, (select id from programs where code = 'HIV'), true, 'U', 21, 'Remarks');
