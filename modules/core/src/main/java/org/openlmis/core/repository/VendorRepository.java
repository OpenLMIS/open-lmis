package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.repository.mapper.VendorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class VendorRepository {

  @Autowired
  VendorMapper mapper;

  public Vendor getByName(String name) {
    return mapper.getByName(name);
  }

  public String getToken(String vendorName) {
    return mapper.getToken(vendorName);
  }
}
