/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.vaccine.service.smt;

import org.openlmis.vaccine.domain.smt.VaccineTarget;
import org.openlmis.vaccine.repository.smt.VaccineTargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VaccineTargetService {

    @Autowired
    private VaccineTargetRepository vaccineTargetRepository;

    public void updateVaccineTarget(VaccineTarget vaccineTarget) {

        if(vaccineTarget.getId() == null){
            vaccineTargetRepository.insertVaccineTraget(vaccineTarget);
        }
        else
            vaccineTargetRepository.updateVaccineTraget(vaccineTarget);
    }

    public List<VaccineTarget> getVaccineTargets() {
        return vaccineTargetRepository.getVaccineTargets();
    }

    public VaccineTarget getVaccineTarget(Long id) {
        return vaccineTargetRepository.getVaccineTarget(id);
    }

    public void deleteVaccineTarget(Long id) {
        vaccineTargetRepository.deleteVaccineTarget(id);
    }

}
