package org.openlmis.stockmanagement.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.stockmanagement.repository.LotRepository;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class LotServiceTest {

  @Mock
  private LotRepository lotRepository;

  @InjectMocks
  private LotService lotService;

  private Product product;

  @Before
  public void setup() {
    product = make(a(ProductBuilder.defaultProduct));
  }

  @Test
  public void shouldCreateLotIfNotExist() throws Exception {
    Date expirationDate = new Date();
    when(lotRepository.getLotByLotNumberAndProductId("ABC", product.getId())).thenReturn(null);

    lotService.getOrCreateLotByLotNumberAndProduct("ABC", expirationDate, product, 1L);
    verify(lotRepository).createLotWithLotNumberAndExpirationDateAndProductId("ABC", expirationDate, product, 1L);
  }

  @Test
  public void shouldNotInsertConflictIfExistingLotAndNewLotHaveSameInfo() throws Exception {
    Date expirationDate = new Date();
    Lot lot = new Lot();
    lot.setLotCode("ABC");
    lot.setExpirationDate(expirationDate);
    lot.setProduct(product);

    when(lotRepository.getLotByLotNumberAndProductId("ABC", product.getId())).thenReturn(lot);

    lotService.getOrCreateLotByLotNumberAndProduct(lot.getLotCode(), lot.getExpirationDate(), lot.getProduct(), 1L);
    verify(lotRepository, never()).saveLotConflict(anyLong(), any(Date.class), anyLong());
  }

  @Test
  public void shouldInsertConflictIfExistingLotAndNewLotHaveDiffExpDates() throws Exception {
    Date expirationDate = new Date();
    Date newExpirationDate = DateUtil.parseDate("2016-05-31", DateUtil.FORMAT_DATE);
    Lot lot = new Lot();
    lot.setId(10L);
    lot.setLotCode("ABC");
    lot.setExpirationDate(expirationDate);
    lot.setProduct(product);

    when(lotRepository.getLotByLotNumberAndProductId("ABC", product.getId())).thenReturn(lot);

    lotService.getOrCreateLotByLotNumberAndProduct(lot.getLotCode(), newExpirationDate, lot.getProduct(), 1L);
    verify(lotRepository).saveLotConflict(10L, newExpirationDate, 1L);
  }
}