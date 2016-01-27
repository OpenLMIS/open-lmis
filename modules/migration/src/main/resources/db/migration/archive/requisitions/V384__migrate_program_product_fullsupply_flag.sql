update program_products set fullSupply = (select (fullsupply) from products p where p.id = program_products.productId);
