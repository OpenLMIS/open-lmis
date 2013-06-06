/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.repository.ProductGroupRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProductGroupServiceTest {

  @Mock
  private ProductGroupRepository productGroupRepository;

  @Test
  public void shouldSaveProductGroup() throws Exception {
    ProductGroupService service = new ProductGroupService(productGroupRepository);
    ProductGroup productGroup = new ProductGroup();

    service.save(productGroup);

    verify(productGroupRepository).save(productGroup);
  }
}
