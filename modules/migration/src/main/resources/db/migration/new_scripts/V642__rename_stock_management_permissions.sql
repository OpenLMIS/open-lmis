DO $$
BEGIN
  DELETE FROM rights WHERE name = 'ADJUST_STOCK';

  DELETE FROM rights WHERE name = 'RECEIVE_STOCK';

  INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
   ('MANAGE_STOCK','REQUISITION','right.manage.stock','Permission to manage stock (issue/receive/adjust)');
END
$$