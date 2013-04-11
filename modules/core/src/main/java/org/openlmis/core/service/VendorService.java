package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class VendorService {

  @Autowired
  VendorRepository repository;

  public Vendor getByName(String name) {
    return repository.getByName(name);
  }
}
