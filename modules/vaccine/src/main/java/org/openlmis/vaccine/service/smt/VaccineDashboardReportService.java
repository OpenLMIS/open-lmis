/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.service.smt;

import lombok.NoArgsConstructor;
import org.openlmis.vaccine.domain.GeoZoneTree;
import org.openlmis.vaccine.repository.smt.VaccineDashboardReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Service
public class VaccineDashboardReportService {

    @Autowired
    private VaccineDashboardReportRepository vaccineReportRepository;


    /*public GeoZoneFacility getGeoZoneFacilityTree(Long userId) {
        List<GeoZoneFacility> zones = vaccineReportRepository.getGeoZoneFacilityTreeForUser(userId);
        GeoZoneFacility tree = vaccineReportRepository.getParentZoneTree();
        populateChildren(tree, zones);
        return tree;
    }
*/
    public GeoZoneTree getGeoZoneTree(Long userId) {
        List<GeoZoneTree> zones = vaccineReportRepository.getGeoZoneTreeForUser(userId);
        GeoZoneTree tree = vaccineReportRepository.getParentZoneTree();
        populateChildren(tree, zones);
        return tree;
    }

    private void populateChildren(GeoZoneTree tree, List<GeoZoneTree> source) {
        // find children from the source
        List<GeoZoneTree> children = new ArrayList<>();
        for (GeoZoneTree t : source) {
            if (t.getParentId() == tree.getId()) {
                children.add(t);
            }
        }

        tree.setChildren(children);
        tree.setFacilityList(vaccineReportRepository.getFacilitiesForGeoZone(tree.getId()));

        for (GeoZoneTree zone : tree.getChildren()) {
            populateChildren(zone, source);
        }
    }

}
