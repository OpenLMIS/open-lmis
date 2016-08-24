package org.openlmis.restapi.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.ArgumentCaptor;
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
import org.openlmis.core.repository.SyncUpHashRepository;
import org.openlmis.core.service.ProductService;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.StockCardDTO;
import org.openlmis.restapi.domain.StockCardMovementDTO;
import org.openlmis.stockmanagement.builder.StockEventBuilder;
import org.openlmis.stockmanagement.domain.*;
import org.openlmis.stockmanagement.dto.LotEvent;
import org.openlmis.stockmanagement.dto.StockEvent;
import org.openlmis.stockmanagement.service.StockCardService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

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
    private SyncUpHashRepository syncUpHashRepository;
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
    private StockEvent stockEvent2;
    private String reasonName;
    private Long quantity;
    private StockEvent stockEvent1;
    private Long userId;

    @Before
    public void setUp() throws Exception {
        setupStockData();
        when(facilityRepository.getById(facilityId)).thenReturn(defaultFacility);
        when(productService.getByCode(productCode)).thenReturn(defaultProduct);

        StockCard expectedStockCard = StockCard.createZeroedStockCard(defaultFacility, defaultProduct);
        when(stockCardService.getOrCreateStockCard(facilityId, productCode, 123L)).thenReturn(expectedStockCard);
    }

    @Test
    public void shouldThrowDataExceptionIfFacilityIdIsInvalid() throws Exception {
        expectedException.expect(DataException.class);
        expectedException.expectMessage("error.facility.unknown");
        when(facilityRepository.getById(facilityId)).thenReturn(null);

        restStockCardService.adjustStock(facilityId, stockEventList, userId);
    }

    @Test
    public void shouldThrowDataExceptionIfInvalidAdjustment() throws Exception {
        expectedException.expect(DataException.class);
        expectedException.expectMessage("error.stockmanagement.invalidadjustment");
        mockReasonWithName("some reason", true);
        stockEvent2.setReasonName(null);

        restStockCardService.adjustStock(facilityId, stockEventList, userId);
    }

    @Test
    public void shouldThrowDataExceptionIfProductIsInvalid() throws Exception {
        expectedException.expect(DataException.class);
        expectedException.expectMessage("error.product.unknown");

        when(productService.getByCode(productCode)).thenReturn(null);

        restStockCardService.adjustStock(facilityId, stockEventList, userId);
    }

    @Test
    public void shouldThrowDataExceptionIfAdjustmentReasonIsInvalid() throws Exception {
        expectedException.expect(DataException.class);
        expectedException.expectMessage("error.stockadjustmentreason.unknown");

        String reasonName = "invalid reason";
        stockEvent2.setReasonName(reasonName);
        when(stockAdjustmentReasonRepository.getAdjustmentReasonByName(reasonName)).thenReturn(null);

        restStockCardService.adjustStock(facilityId, stockEventList, userId);
    }

    @Test
    public void shouldCreateOrUpdateStockCardIfAdjustmentIsValid() throws Exception {
        String productCode2 = "P2";
        Product product2 = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, productCode2)));
        stockEvent2.setProductCode(productCode2);

        when(productService.getByCode(productCode2)).thenReturn(product2);
        mockReasonWithName(reasonName, true);

        when(stockCardService.getOrCreateStockCard(facilityId, productCode, 123L)).thenReturn(StockCard.createZeroedStockCard(defaultFacility, defaultProduct));
        when(stockCardService.getOrCreateStockCard(facilityId, productCode2, 123L)).thenReturn(StockCard.createZeroedStockCard(defaultFacility, product2));

        restStockCardService.adjustStock(facilityId, stockEventList, userId);
        verify(stockCardService).getOrCreateStockCard(facilityId, productCode, 123L);
        verify(stockCardService).getOrCreateStockCard(facilityId, productCode2, 123L);
    }

    @Test
    public void shouldAddStockCardEntriesWithAdjustmentReasonToStockCard() throws Exception {
        String stockAdjustmentReasonName = "Issue";
        StockAdjustmentReason stockAdjustmentReason = new StockAdjustmentReason();
        stockAdjustmentReason.setName(stockAdjustmentReasonName);
        stockAdjustmentReason.setAdditive(false);
        stockEvent1.setReasonName(stockAdjustmentReasonName);
        String stockAdjustmentReasonName2 = "Acquire";
        StockAdjustmentReason stockAdjustmentReason2 = new StockAdjustmentReason();
        stockAdjustmentReason2.setName(stockAdjustmentReasonName2);
        stockAdjustmentReason2.setAdditive(true);
        stockEvent2.setReasonName(stockAdjustmentReasonName2);

        when(stockAdjustmentReasonRepository.getAdjustmentReasonByName(stockAdjustmentReasonName)).thenReturn(stockAdjustmentReason);
        when(stockAdjustmentReasonRepository.getAdjustmentReasonByName(stockAdjustmentReasonName2)).thenReturn(stockAdjustmentReason2);

        List<StockCardEntry> stockCardEntries = restStockCardService.adjustStock(facilityId, stockEventList, userId);

        verify(stockCardService).addStockCardEntries(anyList());
        verify(stockCardService).updateAllStockCardSyncTimeForFacilityToNow(facilityId);

        assertThat(stockCardEntries.get(0).getAdjustmentReason(), is(stockAdjustmentReason));
        assertThat(stockCardEntries.get(1).getAdjustmentReason(), is(stockAdjustmentReason2));
    }

    @Test
    public void shouldAddStockCardEntriesWithPositiveQuantityToStockCardIfReasonIsAdditive() throws Exception {
        String stockAdjustmentReasonName = "Acquire";
        StockAdjustmentReason stockAdjustmentReason = new StockAdjustmentReason();
        stockAdjustmentReason.setName(stockAdjustmentReasonName);
        stockAdjustmentReason.setAdditive(true);
        stockEvent1.setReasonName(stockAdjustmentReasonName);
        stockEvent2.setReasonName(stockAdjustmentReasonName);

        Long quantity1 = 1000L;
        Long quantity2 = 3000L;
        stockEvent1.setQuantity(quantity1);
        stockEvent2.setQuantity(quantity2);

        when(stockAdjustmentReasonRepository.getAdjustmentReasonByName(stockAdjustmentReasonName)).thenReturn(stockAdjustmentReason);

        List<StockCardEntry> stockCardEntries = restStockCardService.adjustStock(facilityId, stockEventList, userId);

        assertThat(stockCardEntries.get(0).getQuantity(), is(1000L));
        assertThat(stockCardEntries.get(1).getQuantity(), is(3000L));
    }

    @Test
    public void shouldAddStockCardEntriesWithPositiveQuantityToStockCardIfReasonIsNotAdditive() throws Exception {
        String stockAdjustmentReasonName = "Issue";
        StockAdjustmentReason stockAdjustmentReason = new StockAdjustmentReason();
        stockAdjustmentReason.setName(stockAdjustmentReasonName);
        stockAdjustmentReason.setAdditive(false);
        stockEvent1.setReasonName(stockAdjustmentReasonName);
        stockEvent2.setReasonName(stockAdjustmentReasonName);

        Long quantity1 = 1000L;
        Long quantity2 = 3000L;
        stockEvent1.setQuantity(quantity1);
        stockEvent2.setQuantity(quantity2);

        when(stockAdjustmentReasonRepository.getAdjustmentReasonByName(stockAdjustmentReasonName)).thenReturn(stockAdjustmentReason);

        List<StockCardEntry> stockCardEntries = restStockCardService.adjustStock(facilityId, stockEventList, userId);

        assertThat(stockCardEntries.get(0).getQuantity(), is(-1000L));
        assertThat(stockCardEntries.get(1).getQuantity(), is(-3000L));
    }

    @Test
    public void shouldAddStockCardEntriesWithUserId() throws Exception {
        mockReasonWithName(reasonName, true);

        List<StockCardEntry> stockCardEntries = restStockCardService.adjustStock(facilityId, stockEventList, userId);

        assertThat(stockCardEntries.get(0).getCreatedBy(), is(userId));
        assertThat(stockCardEntries.get(0).getModifiedBy(), is(userId));

    }

    @Test
    public void shouldAddStockEventCustomPropsValues() throws Exception {
        HashMap<String, String> expirationDates = new HashMap<>();
        expirationDates.put("expirationDates", "10/10/2016, 11/11/2016");
        stockEvent1.setCustomProps(expirationDates);

        mockReasonWithName(reasonName, true);

        List<StockCardEntry> stockCardEntries = restStockCardService.adjustStock(facilityId, stockEventList, userId);

        assertTrue(stockCardEntries.get(0).getExtensions().get(0).getValue().equals("10/10/2016, 11/11/2016"));
        assertEquals(0, stockCardEntries.get(1).getExtensions().size());
    }

    @Test
    public void shouldSaveStockEventOccurredDateToStockCardEntry() {
        Date occurred1 = new Date();
        stockEvent1.setOccurred(occurred1);
        Date occurred2 = new Date();
        stockEvent2.setOccurred(occurred2);

        mockReasonWithName(reasonName, true);

        List<StockCardEntry> stockCardEntries = restStockCardService.adjustStock(facilityId, stockEventList, userId);

        assertTrue(stockCardEntries.get(0).getOccurred().equals(occurred1));
        assertTrue(stockCardEntries.get(1).getOccurred().equals(occurred2));
    }

    @Test
    public void shouldSaveStockEventDocumentNumberToStockCardEntry() {
        String referenceNumber1 = "123";
        stockEvent1.setReferenceNumber(referenceNumber1);
        String referenceNumber2 = "456";
        stockEvent2.setReferenceNumber(referenceNumber2);

        mockReasonWithName(reasonName, true);

        List<StockCardEntry> stockCardEntries = restStockCardService.adjustStock(facilityId, stockEventList, userId);

        assertTrue(stockCardEntries.get(0).getReferenceNumber().equals(referenceNumber1));
        assertTrue(stockCardEntries.get(1).getReferenceNumber().equals(referenceNumber2));
    }

    @Test
    public void shouldNotCreateNewStockCardReferenceWhenTheCardAlreadyExists() {
        mockReasonWithName(reasonName, true);
        restStockCardService.adjustStock(facilityId, stockEventList, userId);
        verify(stockCardService).getOrCreateStockCard(facilityId, productCode, 123L); //should only invoke once
    }

    @Test
    public void shouldTransformStockCardToDTOWhenQueryStockCard() {
        StockCard stockCard = new StockCard();
        Product product = new Product();
        product.setId(1L);
        product.setCode("08ZH5");
        stockCard.setTotalQuantityOnHand(10L);
        stockCard.setProduct(product);
        long quantity = 1L;
        String signature = "myname";
        String expirationDates = "2015-10-01";
        stockCard.setEntries(asList(setupStockCardEntryData(quantity, signature, expirationDates)));

        Date start = DateUtil.parseDate("2015-10-10", DateUtil.FORMAT_DATE);
        Date end = DateUtil.parseDate("2015-10-11", DateUtil.FORMAT_DATE);


        when(stockCardService.queryStockCardByOccurred(facilityId, start, end)).thenReturn(asList(stockCard));
        List<StockCardDTO> stockCardDTOs = restStockCardService.queryStockCardByOccurred(facilityId, start, end);
        StockCardDTO stockCardDTO = stockCardDTOs.get(0);

        assertThat(product.getCode(), is(stockCardDTO.getProduct().getCode()));
        assertThat(product.getId(), is(stockCardDTO.getProduct().getId()));
        assertThat(stockCard.getTotalQuantityOnHand(), is(stockCardDTO.getStockOnHand()));
        StockCardMovementDTO stockCardMovementDTO = stockCardDTO.getStockMovementItems().get(0);
        assertThat(quantity, is(stockCardMovementDTO.getMovementQuantity()));
        assertThat(signature, is(stockCardMovementDTO.getExtensions().get("signature")));
        assertThat(expirationDates, is(stockCardMovementDTO.getExtensions().get("expirationdates")));
    }


    @Test
    public void shouldUpdateAllStockCardsUpdateDateWhenListIsEmpty() throws Exception {
        restStockCardService.updateStockCardSyncTime(123L, new ArrayList<String>());

        verify(stockCardService).updateAllStockCardSyncTimeForFacilityToNow(123L);
    }

    @Test
    public void shouldUpdateStockCardsUpdateDate() throws Exception {
        List<String> stockCardProductCodeList = asList("P1");
        restStockCardService.updateStockCardSyncTime(123L, stockCardProductCodeList);

        verify(stockCardService).updateStockCardSyncTimeToNow(123L, stockCardProductCodeList);
    }

    @Test
    public void shouldNotSaveStockCardEntriesWhenHashExists() throws Exception {
        //given
        when(syncUpHashRepository.hashExists(anyString())).thenReturn(true);
        mockReasonWithName(reasonName, true);

        //when
        List<StockCardEntry> savedEntries = restStockCardService.adjustStock(facilityId, stockEventList, userId);

        //then
        verify(syncUpHashRepository, never()).save(anyString());
        assertThat(savedEntries.size(), is(0));
    }

    @Test
    public void shouldSaveStockCardEntriesWhenHashDoesNotExist() throws Exception {
        //given
        when(syncUpHashRepository.hashExists(anyString())).thenReturn(false);
        mockReasonWithName(reasonName, true);

        //when
        List<StockCardEntry> savedEntries = restStockCardService.adjustStock(facilityId, stockEventList, userId);

        //then
        verify(syncUpHashRepository, times(2)).save(anyString());
        assertThat(savedEntries.size(), is(2));
    }

    @Test
    public void shouldSaveNewLotInformationIfStockEventIncludesLot() throws Exception {
        when(syncUpHashRepository.hashExists(anyString())).thenReturn(false);
        mockReasonWithName(reasonName, true);

        LotEvent lotEvent1 = new LotEvent("lotNumber1", new Date(), 10L);
        Map<String, String> keyValues = new HashMap<>();
        keyValues.put("SOH", "10");
        lotEvent1.setCustomProps(keyValues);
        LotEvent lotEvent2 = new LotEvent("lotNumber2", new Date(), 20L);
        LotEvent lotEvent3 = new LotEvent("lotNumber3", new Date(), 30L);
        stockEventList.get(0).setLotEventList(asList(lotEvent1, lotEvent2, lotEvent3));

        List<StockCardEntry> savedEntries = restStockCardService.adjustStock(facilityId, stockEventList, userId);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(stockCardService).addStockCardEntries(captor.capture());
        List<List> captorAllValues = captor.getAllValues();
        assertEquals(3, ((StockCardEntry) captorAllValues.get(0).get(0)).getLotMovementItems().size());
        assertEquals("lotNumber1", ((StockCardEntry) captorAllValues.get(0).get(0)).getLotMovementItems().get(0).getLot().getLotCode());
        assertEquals(10L, ((StockCardEntry) captorAllValues.get(0).get(0)).getLotMovementItems().get(0).getQuantity(), 0L);
        assertEquals("10", ((StockCardEntry) captorAllValues.get(0).get(0)).getLotMovementItems().get(0).getExtensions().get(0).getValue());
        assertEquals(10L, ((StockCardEntry) captorAllValues.get(0).get(0)).getLotOnHandList().get(0).getQuantityOnHand(), 10L);
        assertThat(savedEntries.size(), is(2));
    }

    @Test
    public void shouldUpdateExistingLotInfoIfStockEventIncludesLot() throws Exception {
        when(syncUpHashRepository.hashExists(anyString())).thenReturn(false);
        mockReasonWithName("RECEIVE", true);
        mockReasonWithName("ISSUE", false);

        String productCode = "ABC";
        Product productForStockCardInLotTest = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, productCode)));
        StockCard stockCardForLotsTest = new StockCard();
        stockCardForLotsTest.setProduct(productForStockCardInLotTest);

        when(stockCardService.getOrCreateStockCard(facilityId, productForStockCardInLotTest.getCode(), userId)).thenReturn(stockCardForLotsTest);

        //set up stock movement info
        List<StockEvent> newStockEventList = new ArrayList<>();
        StockEvent newStockEvent1 = make(a(StockEventBuilder.defaultStockEvent,
                with(StockEventBuilder.productCode, productForStockCardInLotTest.getCode()),
                with(StockEventBuilder.reasonName, "RECEIVE"),
                with(StockEventBuilder.quantity, quantity)));
        LotEvent lotEvent1 = new LotEvent("lotNumber1", new Date(), 10L);
        newStockEvent1.setLotEventList(asList(lotEvent1));
        newStockEventList.add(newStockEvent1);

        StockEvent newStockEvent2 = make(a(StockEventBuilder.defaultStockEvent,
                with(StockEventBuilder.productCode, productForStockCardInLotTest.getCode()),
                with(StockEventBuilder.reasonName, "ISSUE"),
                with(StockEventBuilder.quantity, quantity)
        ));
        LotEvent lotEvent2 = new LotEvent("lotNumber2", new Date(), 10L);
        newStockEvent2.setLotEventList(asList(lotEvent2));
        newStockEventList.add(newStockEvent2);

        when(productService.getByCode(newStockEvent1.getProductCode())).thenReturn(productForStockCardInLotTest);
        when(productService.getByCode(newStockEvent2.getProductCode())).thenReturn(productForStockCardInLotTest);

        //set up existing lot
        Lot lot = new Lot();
        lot.setProduct(make(a(ProductBuilder.defaultProduct)));
        lot.setLotCode(lotEvent1.getLotNumber());
        lot.setExpirationDate(lotEvent1.getExpirationDate());
        LotOnHand lotOnHand = new LotOnHand();
        lotOnHand.setLot(lot);
        lotOnHand.setQuantityOnHand(100L);
        lotOnHand.setStockCard(stockCardForLotsTest);
        when(stockCardService.getLotOnHandByLotNumberAndProductCode(lotEvent1.getLotNumber(), productCode)).thenReturn(lotOnHand);

        Lot lot2 = new Lot();
        lot2.setProduct(make(a(ProductBuilder.defaultProduct)));
        lot2.setLotCode(lotEvent1.getLotNumber());
        lot2.setExpirationDate(lotEvent1.getExpirationDate());
        LotOnHand lotOnHand2 = new LotOnHand();
        lotOnHand2.setLot(lot2);
        lotOnHand2.setQuantityOnHand(100L);
        lotOnHand2.setStockCard(stockCardForLotsTest);
        when(stockCardService.getLotOnHandByLotNumberAndProductCode(lotEvent2.getLotNumber(), productCode)).thenReturn(lotOnHand2);

        restStockCardService.adjustStock(facilityId, newStockEventList, userId);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(stockCardService).addStockCardEntries(captor.capture());
        List<List> captorAllValues = captor.getAllValues();

        assertEquals(110L, ((StockCardEntry) captorAllValues.get(0).get(0)).getLotOnHandList().get(0).getQuantityOnHand(), 0L);
        assertEquals(90L, ((StockCardEntry) captorAllValues.get(0).get(1)).getLotOnHandList().get(0).getQuantityOnHand(), 0L);
    }

    private void mockReasonWithName(String reasonName, boolean additive) {
        StockAdjustmentReason stockAdjustmentReason = new StockAdjustmentReason();
        stockAdjustmentReason.setAdditive(additive);
        when(stockAdjustmentReasonRepository.getAdjustmentReasonByName(reasonName)).thenReturn(stockAdjustmentReason);
    }

    private void setupStockData() {
        facilityId = 1L;
        productCode = "P123";
        reasonName = "some reason";
        quantity = 100L;
        userId = 123L;
        defaultFacility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.facilityId, facilityId)));
        defaultProduct = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, productCode)));

        stockEventList = new ArrayList<>();
        stockEvent1 = make(a(StockEventBuilder.defaultStockEvent,
                with(StockEventBuilder.productCode, productCode),
                with(StockEventBuilder.reasonName, reasonName),
                with(StockEventBuilder.quantity, quantity)));
        stockEventList.add(stockEvent1);
        stockEvent2 = make(a(StockEventBuilder.defaultStockEvent,
                with(StockEventBuilder.productCode, productCode),
                with(StockEventBuilder.reasonName, reasonName),
                with(StockEventBuilder.quantity, quantity)
        ));
        stockEventList.add(stockEvent2);
    }


    private StockCardEntry setupStockCardEntryData(long quantity, String signature, String expirationDates) {
        StockCardEntry stockCardEntry = new StockCardEntry();
        stockCardEntry.setQuantity(quantity);
        stockCardEntry.setAdjustmentReason(new StockAdjustmentReason());
        ArrayList<StockCardEntryKV> extensions = new ArrayList<>();
        extensions.add(stockCArdEntryBuilder("signature", signature, 0));
        extensions.add(stockCArdEntryBuilder("expirationdates", expirationDates, 1));
        stockCardEntry.setExtensions(extensions);
        return stockCardEntry;
    }

    private StockCardEntryKV stockCArdEntryBuilder(String key, String value, int index) {
        StockCardEntryKV element = new StockCardEntryKV();
        element.setKey(key);
        element.setValue(value);
        return element;
    }
}