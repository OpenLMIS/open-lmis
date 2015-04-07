/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.vaccine.repository.smt;

import org.openlmis.vaccine.domain.smt.VaccineTarget;
import org.openlmis.vaccine.repository.mapper.VaccineTargetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VaccineTargetRepository {

    @Autowired
    private VaccineTargetMapper vaccineTargetMapper;

    public void updateVaccineTraget(VaccineTarget vaccineTarget) {
        vaccineTargetMapper.updateVaccineTraget(vaccineTarget);
    }

    public List<VaccineTarget> getVaccineTargets() {
        return vaccineTargetMapper.getVaccineTargets();
    }

    public VaccineTarget getVaccineTarget(Long id) {    return  vaccineTargetMapper.getVaccineTarget(id); }

    public void insertVaccineTraget(VaccineTarget vaccineTarget) {vaccineTargetMapper.insertVaccineTraget(vaccineTarget);   }

    public void deleteVaccineTarget(Long id) {
        vaccineTargetMapper.deleteVaccineTarget(id);
    }}
