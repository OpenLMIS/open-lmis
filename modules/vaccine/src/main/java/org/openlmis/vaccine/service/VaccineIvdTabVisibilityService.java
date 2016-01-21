/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.vaccine.service;

import org.openlmis.vaccine.domain.config.VaccineIvdTabVisibility;
import org.openlmis.vaccine.repository.VaccineIvdTabVisibilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.openlmis.vaccine.utils.ListUtil.emptyIfNull;

@Service
public class VaccineIvdTabVisibilityService {

  @Autowired
  VaccineIvdTabVisibilityRepository repository;

  public List<VaccineIvdTabVisibility> getVisibilityForProgram(Long programId){
    List<VaccineIvdTabVisibility> visibilities = repository.getVisibilityForProgram(programId);
    if(emptyIfNull(visibilities).isEmpty()){
      return repository.getAllVisibilityConfiguration();
    }
    return visibilities;
  }

  public void save(List<VaccineIvdTabVisibility> visibilities, Long programId){
    for(VaccineIvdTabVisibility visibility : visibilities){
      if(!visibility.hasId()){
        visibility.setProgramId(programId);
        repository.insert(visibility);
      }else{
        repository.update(visibility);
      }
    }
  }
}
