/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.SupplyLineRepository;
import org.openlmis.core.repository.SupplyLineRepositoryExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class SupplyLineServiceExtension {

    @Autowired
    private SupplyLineRepositoryExtension supplyLineRepositoryExt;

    @Autowired
    private SupplyLineRepository supplyLineRepository;

    public List<SupplyLine> getAllSupplyLine(){
        return supplyLineRepositoryExt.getAllSupplyLine();
    }

    public SupplyLine getSupplylineById(Long id) {
        return supplyLineRepositoryExt.getSupplylineById(id);
    }

    public SupplyLine getSupplylineDetailById(Long id) {
        return supplyLineRepositoryExt.getSupplylineDetailById(id);
    }

    public void deleteById(Long supplyLineId) {
        this.supplyLineRepositoryExt.deleteById(supplyLineId);
    }


}
