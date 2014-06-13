/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.repository.ProductFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing {@link org.openlmis.core.domain.ProductForm} entities.
 */
@Service
@NoArgsConstructor
public class ProductFormService {

  private ProductFormRepository pfRep;

  @Autowired
  public ProductFormService(ProductFormRepository productFormRepository) {pfRep = productFormRepository;}

  public ProductForm getExisting(ProductForm pf) {return pfRep.getByCode(pf.getCode());}

  public void save(ProductForm pf) {
    if(pf.hasId()) pfRep.update(pf);
    else pfRep.insert(pf);
  }
}
