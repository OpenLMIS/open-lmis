insert into Master_RnR_Template(column_name, column_label, description , column_position, default_value, available_sources, data_source, formula, column_indicator, is_used, is_visible, is_mandatory)
    values
    ('product_code','Product Code', 'Unique identifier for each commodity', 1, '', 'Calculated','Reference Value (Product Table)', '' ,'O', true, true, true),
    ('product','Product', 'Primary name of the product', 2,  '', 'Calculated', 'Reference Value (Product Table)', '' ,'R', true, true, true),
    ('unit_of_issue', 'Unit/Unit of Issue', 'Dispensing unit for this product',3,'', 'Calculated', 'Reference Value (Product Table)','','U',true,true,true),
    ('beginning_balance', 'Beginning Balance', 'Stock in hand of previous period.This is quantified in dispensing units',4,'0','User Input', 'User Input','','A',true,true,false),
    ('quantity_received',' Total Received Quantity','Total quantity received in last period.This is quantified in dispensing units',5,'0', 'User Input', 'User Input','','B',TRUE,TRUE,false),
    ('quantity_dispensed','Total Consumed Quantity','Quantity dispensed/consumed in last reporting  period. This is quantified in dispensing units',6,'0','User Input/Calculated', 'User Input/Calculated','A + B (+/-) D - E','C',TRUE,TRUE,false),
    ('losses_and_adjustments','Total Losses / Adjustments','All kind of looses/adjustments made at the facility',7,'0','User Input', 'User Input','D1 + D2+D3...DN','D',TRUE,TRUE,false),
    ('reason_for_losses_and_adjustments','Reason for Losses and Adjustments','Type of Losses/adjustments',8,NULL,'Calculated', 'Reference Value ( Table)','','S',TRUE,TRUE,false),
    ('stock_in_hand','Stock on Hand','Current physical count of stock on hand. This is quantified in dispensing units',9,'0','User Input/Calculated', 'User Input/Calculated','A+B(+/-)D-C','E',TRUE,TRUE,false),
    ('new_patient_count','Total number of new patients added to service on the program','Total of new patients introduced',10,'0','User Input', 'User Input','','F',TRUE,TRUE,FALSE),
    ('stock_out_days','Total Stockout days','Total number of days facility was out of stock',11,'0','User Input','User Input','','X',TRUE,TRUE,FALSE),
    ('normalized_consumption','Adjusted Total Consumption','Total quantity consumed after adjusting for stockout days. This is quantified in dispensing units',12,NULL,'Calculated', 'Calculated','C * (M*30)/((M*30)-X) + ( F* No of tabs per month * 1)' ,'N',TRUE,TRUE,FALSE),
    ('amc','Average Monthly Consumption(AMC)','Average Monthly consumption, for last three months. This is quantified in dispensing units',13,'Default = N','Calculated','Calculated','(N/M + Ng-1/M + ...Ng-(g-1)/M)/G','P',TRUE,TRUE,FALSE),
    ('max_stock_quantity' ,'Maximum Stock Quantity' ,'Maximum Stock calculated based on consumption and max months of stock.This is quantified in dispensing units',14,'0','Calculated','Calculated','P * MaxMonthsStock','H',TRUE,TRUE,FALSE),
    ('calculated_order_quantity','Calculated Order Quantity','Actual Quantity needed after deducting stock in hand. This is quantified in dispensing units',15,'0','Calculated','Calculated','H - E','I',TRUE,TRUE,FALSE),
    ('quantity_requested','Requested Quantity','Requested override of calculated quantity.This is quantified in dispensing units',16,NULL,'User Input','User Input','','J',TRUE,TRUE,FALSE),
    ('reason_for_requested_quantity','Requested Quantity Explanation','Explanation of request for a quatity other than calculated order quantity.',17,NULL,'User Input','User Input','','W',TRUE,TRUE,FALSE),
    ('quantity_approved','Approved Quantity','Final approved quantity. This is quantified in dispensing units',18,'Default = I','User Input','User Input','','K',TRUE,TRUE,FALSE),
    ('packs_to_ship','Packs to Ship','Total packs to be shipped based on pack size and applying rounding rules ',19,'0','Calculated','Calculated','K / U + Rounding rules','V',TRUE,TRUE,FALSE),
    ('Price','Price per pack','Price per Pack. It defaults to zero if not specified.',20,NULL,'Calculated', 'Reference value (CostHistory Table)','','T',TRUE,TRUE,FALSE),
    ('cost','Total cost','Total cost of the product. This will be zero if price is not defined',21,NULL,'Calculated','Calculated','V * T','Q',TRUE,TRUE,FALSE),
    ('remarks','Remarks','Any additional remarks',22,NULL,'User Input','User Input','','L',TRUE,TRUE,FALSE);





