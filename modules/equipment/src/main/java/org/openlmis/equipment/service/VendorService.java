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

package org.openlmis.equipment.service;

import org.openlmis.equipment.domain.Vendor;
import org.openlmis.equipment.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VendorService {

  @Autowired
  private VendorRepository repository;

  public List<Vendor> getAll() {
    return repository.getAll();
  }

  public Vendor getById(Long id) {
    return repository.getById(id);
  }

  public void save(Vendor vendor) {
    if (vendor.getId() == null) {
      repository.insert(vendor);
    } else {
      repository.update(vendor);
    }
  }

  public void removeVendor(Long id) {
    try {
      repository.remove(id);
    } catch (DataIntegrityViolationException ex) {
      throw (ex);
    }

  }
}
