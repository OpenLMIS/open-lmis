/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */


package org.openlmis.equipment.service;

import lombok.NoArgsConstructor;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class DonorService {

  private DonorRepository donorRepository;

  @Autowired
  public DonorService(DonorRepository donorRepository) {
    this.donorRepository = donorRepository;
  }

  public void save(Donor donor) {
    if (donor.getId() == null)
      donorRepository.insert(donor);
    else
      donorRepository.update(donor);
  }

  public List<Donor> getAll(){
    return donorRepository.getAll();
  }

  public List<Donor> getAllWithDetails(){
    return donorRepository.getAllWithDetails();
  }

  public void removeDonor(Long id){
      donorRepository.removeDonor(id);
  }

  public Donor getById(Long donorId){
    return donorRepository.getDonorById(donorId);
  }

}
