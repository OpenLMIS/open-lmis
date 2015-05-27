/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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
    if(emptyIfNull(visibilities).size() == 0){
      return repository.getAllVisibilityConfiguration();
    }
    return visibilities;
  }

  public void save(List<VaccineIvdTabVisibility> visibilities){
    for(VaccineIvdTabVisibility visibility : visibilities){
      if(visibility.getId() == null){
        repository.insert(visibility);
      }else{
        repository.update(visibility);
      }
    }
  }
}
