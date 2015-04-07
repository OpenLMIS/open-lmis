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

import org.openlmis.vaccine.domain.AdministrationMode;
import org.openlmis.vaccine.domain.smt.Dilution;
import org.openlmis.vaccine.domain.smt.VaccinationType;
import org.openlmis.vaccine.domain.smt.VaccineQuantification;
import org.openlmis.vaccine.repository.smt.VaccineQuantificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VaccineQuantificationService {

    @Autowired
    private VaccineQuantificationRepository vaccineQuantificationRepository;

    public void updateVaccineQuantification(VaccineQuantification vaccineQuantification) {

        if(vaccineQuantification.getId() == null){
            vaccineQuantificationRepository.insertVaccineQuantification(vaccineQuantification);
        }
        else
            vaccineQuantificationRepository.updateVaccineQuantification(vaccineQuantification);
    }

    public List<VaccineQuantification> getVaccineQuantifications() {
        return vaccineQuantificationRepository.getVaccineQuantifications();
    }

    public VaccineQuantification getVaccineQuantification(Long id) {
        return vaccineQuantificationRepository.getVaccineQuantification(id);
    }

    public void deleteVaccineQuantification(Long id) {
        vaccineQuantificationRepository.deleteVaccineQuantification(id);
    }

    public List<AdministrationMode> getVaccineAdministrationMode() {
        return vaccineQuantificationRepository.getVaccineAdministrationMode();
    }

    public List<Dilution> getVaccineDilutions() {
        return vaccineQuantificationRepository.getVaccineDilutions();
    }

    public List<VaccinationType> getVaccinationTypes() {
        return vaccineQuantificationRepository.getVaccinationTypes();
    }

}
