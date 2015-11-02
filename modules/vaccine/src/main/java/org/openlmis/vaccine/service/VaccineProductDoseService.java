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

import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.dto.ProductDoseDTO;
import org.openlmis.vaccine.dto.VaccineServiceConfigDTO;
import org.openlmis.vaccine.repository.ProductDoseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VaccineProductDoseService {

  @Autowired
  private ProductDoseRepository repository;

  @Autowired
  private ProgramProductRepository programProductRepository;

  public VaccineServiceConfigDTO getProductDoseForProgram(Long programId) {
    VaccineServiceConfigDTO dto = new VaccineServiceConfigDTO();
    List<ProductDoseDTO> productDoseDTOs = new ArrayList<>();
    List<ProgramProduct> pp = programProductRepository.getActiveByProgram(programId);
    List<Product> products = new ArrayList<>();
    for (ProgramProduct p : pp) {
      if (!p.getProduct().getFullSupply()) {
        continue;
      }
      List<VaccineProductDose> doses = repository.getDosesForProduct(programId, p.getProduct().getId());
      if (!doses.isEmpty()) {
        ProductDoseDTO productDose = new ProductDoseDTO();
        productDose.setProductId(p.getProduct().getId());
        productDose.setProductName(p.getProduct().getPrimaryName());
        productDose.setDoses(doses);
        productDoseDTOs.add(productDose);
      } else {
        //these are the possible other products.
        products.add(p.getProduct());
      }
    }
    dto.setPossibleDoses(repository.getAllDoses());
    dto.setPossibleProducts(products);
    dto.setProtocols(productDoseDTOs);
    return dto;
  }

  public List<VaccineProductDose> getForProgram(Long programId) {
    return repository.getProgramProductDoses(programId);
  }

  public void save(List<ProductDoseDTO> productDoseDTOs) {
    repository.deleteAllByProgram(productDoseDTOs.get(0).getDoses().get(0).getProgramId());
    for (ProductDoseDTO productDose : productDoseDTOs) {
      for (VaccineProductDose dose : productDose.getDoses()) {
        repository.insert(dose);
      }
    }
  }
}
