insert into Master_RnR_Template(column_name, column_label, description , column_position, default_value, data_source, formula, column_indicator, is_used, is_visible, is_mandatory)
    values
    ('product_code','MSD ProductCode', 'This is Unique identifier for each commodity', 1,  '', 'Reference Value (Product Table)', '' ,'O', true, true, true),

    ('beginning_balance', 'Beginning Balance', 'Balance at start of the period/Stock in hand of previous R&R',4,'0','User Input','','A',true,true,true),
    ('quantity_received','Amount Received / Quantity Received','Quantity received from MoH for a period',5,'0','User Input','','B',TRUE,TRUE,true),
    ('quantity_dispensed','Quantity Dispensed','Quantity issued to patients by a facility in last reporting  period',6,'0','User Input','','C',TRUE,TRUE,TRUE),
    ('losses_and_adjustments','Losses / Adjustments','All kind of looses/adjustments made at the facility',7,'0','User Input','D1 + D2+D3…DN','D',TRUE,TRUE,TRUE),
    ('reason_for_losses_and_adjustments','Reason for Losses and Adjustments','Type of Losses/adjustments',8,'','Reference Value ( Table)','','S',TRUE,TRUE,TRUE),
    ('stock_in_hand','Stock on Hand / Ending balance / Closing Balance','This is current physical count of stock in store room',9,'0','User Input','','E',TRUE,TRUE,TRUE),
    ('stock_out_days','Number of days of Outstocks','Days Facility was out of stock',12,'0','User Input','','X',TRUE,TRUE,FALSE),
    ('max_stock_quantity' ,'Maximum Amount Needed / Maximum Stock Quantity' ,'Maximum Stock calculated based on consumption and max a facility can keep',15,'0','Derived','P * MaxMonthsStock','H',TRUE,TRUE,FALSE),
    ('quantity_requested','Requested Amount / Quantity Requested','Quatity requested by a facility',17,NULL,'User Input','','J',TRUE,TRUE,FALSE),
    ('reason_for_requested_quantity','Requested Amount Explanation','Explanation to request more/less',18,'','User Input','','W',TRUE,TRUE,FALSE),
    ('quantity_approved','Approved Amount / Quantity Approved','Final approved Quantity',19,NULL,'User Input','','K',TRUE,TRUE,TRUE),
    ('remarks','Remarks','Any additional remarks',23,NULL,'User Input','','L',TRUE,TRUE,TRUE),

    ('cost','Cost','Total cost',22,NULL,'Derived','V * T','Q',TRUE,TRUE,FALSE),
    ('packs_to_ship','Packs to Ship','Total packs required to ship',21,'0','Derived','K / U + Rounding rules','V',TRUE,TRUE,FALSE),
    ('calculated_order_quantity','Amount Needed / Calculated Order Quantity','Final Quantity needed after deducting stock in hand',16,'0','Derived','H - E','I',TRUE,TRUE,FALSE),
    ('estimated_consumption','Estimated Consumption','Quantity issued to patients by a facility in last reporting  period',10,'','Derived','A + B (+/-) D - E','C',TRUE,TRUE,FALSE),
    ('normalized_consumption','Normalized Consumption','Consumption calculated after considering stock out days',13,NULL,'Derived','C * (M*30)/((M*30)-X) + ( F* No of tabs per month * 1)' ,'N',TRUE,TRUE,FALSE),
    ('amc','(Updated) AMC','Average Monthly consumption',14,NULL,'Derived','(N/M + Ng-1/M + ...Ng-(g-1)/M)/G','P',TRUE,TRUE,FALSE),
    ('new_patient_count','No of New Patients','Total of new patients introduced',11,'0','User Input','','F',TRUE,TRUE,FALSE),

    ('Description /Item/Strength', 'Description /Item/Strength', 'This is Generic name of the commodity printed on it', 2,  '', 'Reference Value (Product Table)', '','R', true, true, true),
    ('Unit/Unit of Issue', 'Unit/Unit of Issue', 'The number of dispensing units in a container, as packaged by the manufacturer, or repackaged and distributed by the MoH warehouse.',3,'','Reference Value (Product Table)','','U',true,true,true),
    ('Price','Price','Price per Pack size',20,NULL,'Refrence value (CostHistory Table)','','T',TRUE,TRUE,FALSE);

