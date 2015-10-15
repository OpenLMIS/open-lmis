package org.openlmis.restapi.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.StockAdjustmentReasonRepository;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.stockmanagement.builder.StockEventBuilder;
import org.openlmis.stockmanagement.dto.StockEvent;
import org.openlmis.stockmanagement.service.StockCardService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RestStockCardServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @InjectMocks
  private RestStockCardService restStockCardService;

  @Mock
  private FacilityRepository facilityRepository;
  @Mock
  private ProductService productService;
  @Mock
  private StockAdjustmentReasonRepository stockAdjustmentReasonRepository;
  @Mock
  private StockCardService stockCardService;

  private Long facilityId;
  private String productCode;
  private List<StockEvent> stockEventList;
  private Facility defaultFacility;
  private Product defaultProduct;
  private StockEvent stockEvent;
  private String reasonName;

  @Test
  public void shouldThrowDataExceptionIfFacilityIdIsInvalid() throws Exception {
    setupStockData();

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.facility.unknown");
    when(facilityRepository.getById(facilityId)).thenReturn(null);
    when(productService.getByCode(productCode)).thenReturn(defaultProduct);

    restStockCardService.adjustStock(facilityId, stockEventList, facilityId);
  }

  @Test
  public void shouldThrowDataExceptionIfInvalidAdjustment() throws Exception {
    setupStockData();

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.stockmanagement.invalidadjustment");
    when(facilityRepository.getById(facilityId)).thenReturn(defaultFacility);
    when(productService.getByCode(productCode)).thenReturn(defaultProduct);
    when(stockAdjustmentReasonRepository.getAdjustmentReasonByName("some reason")).thenReturn(new StockAdjustmentReason());
    stockEvent.setReasonName(null);

    restStockCardService.adjustStock(facilityId, stockEventList, facilityId);
  }

  @Test
  public void shouldThrowDataExceptionIfProductIsInvalid() throws Exception {
    setupStockData();

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.product.unknown");
    when(facilityRepository.getById(facilityId)).thenReturn(defaultFacility);
    when(productService.getByCode(productCode)).thenReturn(null);

    restStockCardService.adjustStock(facilityId, stockEventList, facilityId);
  }

  @Test
  public void shouldThrowDataExceptionIfAdjustmentReasonIsInvalid() throws Exception {
    setupStockData();

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.stockadjustmentreason.unknown");
    when(facilityRepository.getById(facilityId)).thenReturn(defaultFacility);
    when(productService.getByCode(productCode)).thenReturn(defaultProduct);
    String reasonName = "invalid reason";
    stockEvent.setReasonName(reasonName);
    when(stockAdjustmentReasonRepository.getAdjustmentReasonByName(reasonName)).thenReturn(null);

    restStockCardService.adjustStock(facilityId, stockEventList, facilityId);
  }

  @Test
  public void shouldCreateOrUpdateStockCardIfAdjustmentIsValid() throws Exception {
    setupStockData();

    when(facilityRepository.getById(facilityId)).thenReturn(defaultFacility);
    when(productService.getByCode(productCode)).thenReturn(defaultProduct);
    when(stockAdjustmentReasonRepository.getAdjustmentReasonByName(reasonName)).thenReturn(new StockAdjustmentReason());

    restStockCardService.adjustStock(facilityId, stockEventList, facilityId);
    verify(stockCardService, times(2)).getOrCreateStockCard(facilityId, productCode);
  }

  private void setupStockData() {
    facilityId = 1L;
    productCode = "P123";
    reasonName = "some reason";
    defaultFacility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.facilityId, facilityId)));
    defaultProduct = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, productCode)));

    stockEventList = new ArrayList<>();
    StockEvent defaultStockEvent = make(a(StockEventBuilder.defaultStockEvent,
        with(StockEventBuilder.facilityId, facilityId),
        with(StockEventBuilder.productCode, productCode),
        with(StockEventBuilder.reasonName, reasonName)));
    stockEventList.add(defaultStockEvent);
    stockEvent = make(a(StockEventBuilder.defaultStockEvent,
        with(StockEventBuilder.facilityId, facilityId),
        with(StockEventBuilder.productCode, productCode),
        with(StockEventBuilder.reasonName, reasonName)));
    stockEventList.add(stockEvent);
  }
}