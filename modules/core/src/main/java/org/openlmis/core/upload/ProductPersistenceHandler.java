/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("productPersistenceHandler")
public class ProductPersistenceHandler extends AbstractModelPersistenceHandler {

    private ProductService productService;

    @Autowired
    public ProductPersistenceHandler (ProductService productService) {
        this.productService = productService;
    }

    @Override
    protected void save(Importable importable, AuditFields auditFields) {
        Product product = (Product) importable;
        product.setModifiedBy(auditFields.getUser());
        product.setModifiedDate(new Date());
        productService.save(product);
    }
}
