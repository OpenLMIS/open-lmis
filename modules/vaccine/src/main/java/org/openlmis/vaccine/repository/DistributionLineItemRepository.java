/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository;

import org.openlmis.core.exception.DataException;
import org.openlmis.vaccine.domain.DistributionLineItem;
import org.openlmis.vaccine.repository.mapper.DistributionLineItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DistributionLineItemRepository {
    @Autowired
    private DistributionLineItemMapper distributionLineItemMapper;

    public DistributionLineItem update(DistributionLineItem distributionLineItem){
        try {
            if (distributionLineItem.getId() == null) {
                distributionLineItemMapper.insert(distributionLineItem);
            } else {
                distributionLineItemMapper.update(distributionLineItem);
            }
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new DataException("error.duplicate");
        } catch (DataIntegrityViolationException integrityViolationException) {
            String errorMessage = integrityViolationException.getMessage().toLowerCase();
            if (errorMessage.contains("foreign key") || errorMessage.contains("not-null constraint")) {
                throw new DataException("error.reference.data.missing");
            }
            throw new DataException("error.incorrect.length");
        }

        return distributionLineItem;
    }

    public List<DistributionLineItem> getAll(){
        return distributionLineItemMapper.getAll();
    }
}
