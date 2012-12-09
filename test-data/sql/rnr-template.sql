delete from program_rnr_template;
insert into program_rnr_template
(column_id, program_code, is_visible, source, position, label)
values
(1, 'HIV',  true, 'R', 1,  'Product Code'),
(2, 'HIV',  true, 'R', 2,  'Product'),
(3, 'HIV',  true, 'R', 3,  'Unit/Unit of Issue'),
(4, 'HIV',  true, 'U', 4,  'Beginning Balance'),
(5, 'HIV',  true, 'U', 5,  'Total Received Quantity'),
(6, 'HIV',  true, 'U', 6,  'Total Consumed Quantity'),
(7, 'HIV',  true, 'U', 7,  'Total Losses / Adjustments'),
(8, 'HIV',  true, 'U', 8,  'Reason for Losses and Adjustments'),
(9, 'HIV',  true, 'C', 9,  'Stock on Hand'),
(10, 'HIV', true, 'U', 10, 'Total number of new patients added to service on the program'),
(11, 'HIV', true, 'U', 11, 'Total Stockout days'),
(12, 'HIV', true, 'C', 12, 'Adjusted Total Consumption'),
(13, 'HIV', true, 'C', 13, 'Average Monthly Consumption(AMC)'),
(14, 'HIV', true, 'C', 14, 'Maximum Stock Quantity'),
(15, 'HIV', true, 'C', 15, 'Calculated Order Quantity'),
(16, 'HIV', true, 'U', 16, 'Requested Quantity'),
(17, 'HIV', true, 'U', 17, 'Requested Quantity Explanation'),
(18, 'HIV', true, 'U', 18, 'Approved Quantity'),
(19, 'HIV', true, 'C', 19, 'Packs to Ship'),
(20, 'HIV', true, 'R', 20, 'Price per pack'),
(21, 'HIV', true, 'C', 21, 'Total cost'),
(22, 'HIV', true, 'U', 22, 'Remarks');
