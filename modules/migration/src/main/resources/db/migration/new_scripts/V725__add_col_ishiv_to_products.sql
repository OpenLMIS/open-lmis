ALTER TABLE products ADD ishiv boolean default false;

UPDATE products as p SET ishiv = CASE WHEN(tmp.code = 'MMIA' or tmp.parentid=1) THEN true END
FROM (select pt.id,pg.code,pg.parentid from products pt
  left join program_products pp on pt.id = pp.productid
  left join programs pg on pp.programid = pg.id ) as tmp WHERE p.id = tmp.id;