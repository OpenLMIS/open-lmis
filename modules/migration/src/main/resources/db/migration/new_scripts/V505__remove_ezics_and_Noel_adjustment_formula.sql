

delete from order_quantity_adjustment_products where factorid in ( select id from order_quantity_adjustment_factors where name in ('Based on eZICS formula','Based on Noel Watson'));
delete FROM order_quantity_adjustment_factors
where name in ('Based on eZICS formula','Based on Noel Watson');