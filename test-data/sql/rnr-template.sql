delete from program_rnr_template;
insert into program_rnr_template
(column_id, program_code, is_visible, source, label, position)
values
(1, 'HIV',  true, 'R', 'Product Code', 1),
(2, 'HIV',  true, 'R', 'Product', 2),
(3, 'HIV',  true, 'R', 'Unit/Unit of Issue', 3),
(4, 'HIV',  true, 'U', 'Beginning Balance', 4),
(5, 'HIV',  true, 'U', 'Total Received Quantity', 5),
(6, 'HIV',  true, 'U', 'Total Consumed Quantity', 6),
(7, 'HIV',  true, 'U', 'Total Losses / Adjustments', 7),
(8, 'HIV',  true, 'U', 'Reason for Losses and Adjustments', 8),
(9, 'HIV',  true, 'C', 'Stock on Hand', 9),
(10, 'HIV', true, 'U', 'Total number of new patients added to service on the program', 10),
(11, 'HIV', true, 'U', 'Total Stockout days', 11),
(12, 'HIV', true, 'C', 'Adjusted Total Consumption', 12),
(13, 'HIV', true, 'C', 'Average Monthly Consumption(AMC)', 13),
(14, 'HIV', true, 'C', 'Maximum Stock Quantity', 14),
(15, 'HIV', true, 'C', 'Calculated Order Quantity', 15),
(16, 'HIV', true, 'U', 'Requested Quantity', 16),
(17, 'HIV', true, 'U', 'Requested Quantity Explanation', 17),
(18, 'HIV', true, 'U', 'Approved Quantity', 18),
(19, 'HIV', true, 'C', 'Packs to Ship', 19),
(20, 'HIV', true, 'R', 'Price per pack', 20),
(21, 'HIV', true, 'C', 'Total cost', 21),
(22, 'HIV', true, 'U', 'Remarks', 22);
