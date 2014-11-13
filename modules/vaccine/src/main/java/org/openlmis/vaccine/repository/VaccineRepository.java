/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero GenNeral Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository;

import org.openlmis.vaccine.domain.VaccineTarget;
import org.openlmis.vaccine.mapper.VaccineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VaccineRepository {

    @Autowired
    private VaccineMapper vaccineMapper;


    public void updateVaccineTraget(VaccineTarget vaccineTarget) {
        vaccineMapper.updateVaccineTraget(vaccineTarget);
    }

    public List<VaccineTarget> getVaccineTargets() {
        return vaccineMapper.getVaccineTargets();
    }

    public VaccineTarget getVaccineTarget(Long id) {    return  vaccineMapper.getVaccineTarget(id); }

    public void insertVaccineTraget(VaccineTarget vaccineTarget) {vaccineMapper.insertVaccineTraget(vaccineTarget);   }

    public void deleteVaccineTarget(Long id) {
        vaccineMapper.deleteVaccineTarget(id);
    }
}
