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

package org.openlmis.equipment.repository;

import org.openlmis.equipment.domain.MaintenanceRequest;
import org.openlmis.equipment.dto.Log;
import org.openlmis.equipment.repository.mapper.MaintenanceRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MaintenanceRequestRepository {

  @Autowired
  private MaintenanceRequestMapper mapper;

  public MaintenanceRequest getById(Long id){
    return mapper.getById(id);
  }

  public List<MaintenanceRequest> getAllForFacility(Long facilityId){
    return mapper.getAllForFacility(facilityId);
  }

  public List<MaintenanceRequest> getAllForVendor(Long vendorId){
    return mapper.getAllForVendor(vendorId);
  }

  public List<MaintenanceRequest> getOutstandingForVendor(Long vendorId){
    return mapper.getOutstandingRequestsForVendor(vendorId);
  }

  public List<MaintenanceRequest> getOutstandingForUser(Long userId){
    return mapper.getOutstandingRequestsForUser(userId);
  }

  public List<MaintenanceRequest> getAll(){
    return mapper.getAll();
  }

  public void insert(MaintenanceRequest value){
    mapper.insert(value);
  }

  public void update(MaintenanceRequest value){
    mapper.update(value);
  }

  public List<Log> getFullHistory(Long inventoryId){
    return mapper.getFullHistory(inventoryId);
  }
}
