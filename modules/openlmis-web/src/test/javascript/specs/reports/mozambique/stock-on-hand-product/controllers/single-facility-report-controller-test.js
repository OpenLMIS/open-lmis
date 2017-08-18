describe("Single facility Report Controller", function () {
  var scope, facilityProductData, httpBackend, dateFilter, lotExpiryDateService, window, reportExportExcelService,dateFormatService;

  facilityProductData = [
    {
      "drug.drug_code": "01C01",
      "location.province_code": "MAPUTO_PROVINCIA",
      "cmm": -1.0,
      "occurred.month": 1.0,
      "drug.drug_name": "Hidralazina25mg/5mLInject\u00e1vel",
      "location.province_name": "Maputo Prov\u00edncia",
      "facility.facility_name": "Habel Jafar",
      "occurred.day": 12.0,
      "location.district_code": "MARRACUENE",
      "facility.facility_code": "HF8",
      "last_sync_date": "2016-06-21T12:05:46.990453+08:00",
      "expiry_date": "2016-01-30",
      "soh": "5",
      "occurred_date": "2016-01-12",
      "vw_daily_full_soh_facility_name": "Habel Jafar",
      "location.district_name": "Marracuene",
      "occurred.year": 2016.0
    },
    {
      "drug.drug_code": "01C01",
      "location.province_code": "MAPUTO_PROVINCIA",
      "cmm": -1.0,
      "occurred.month": 1.0,
      "drug.drug_name": "Hidralazina25mg/5mLInject\u00e1vel",
      "location.province_name": "Maputo Prov\u00edncia",
      "facility.facility_name": "Habel Jafar",
      "occurred.day": 15.0,
      "location.district_code": "MARRACUENE",
      "facility.facility_code": "HF8",
      "last_sync_date": "2016-06-21T12:05:46.990453+08:00",
      "expiry_date": "2016-01-30",
      "soh": "25",
      "occurred_date": "2016-01-15",
      "vw_daily_full_soh_facility_name": "Habel Jafar",
      "location.district_name": "Marracuene",
      "occurred.year": 2016.0
    }];

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));
  beforeEach(inject(function (_$httpBackend_, $rootScope, $filter, $controller, LotExpiryDateService, $window, ReportExportExcelService, DateFormatService) {
    scope = $rootScope.$new();
    httpBackend = _$httpBackend_;
    dateFilter = $filter('date');
    lotExpiryDateService = LotExpiryDateService;
    window = $window;
    reportExportExcelService = ReportExportExcelService;
    dateFormatService = DateFormatService;
    $controller(SingleFacilityReportController, {$scope: scope});
  }));

  it('should load all product report successfully', function () {
    scope.reportParams.facilityId = undefined;
    scope.reportParams.districtId = 1;
    scope.reportParams.provinceId = 1;

    scope.loadReport();
    expect(scope.invalidFacility).toBe(true);
    scope.reportParams.facilityId = 414;
    scope.reportParams.endTime = '2017-02-04';

    spyOn(lotExpiryDateService, 'populateLotOnHandInformationForSoonestExpiryDate');

    httpBackend.expectGET('/cubesreports/cube/vw_daily_full_soh/facts?cut=occurred%3A-2017%2C02%2C04').respond(200, facilityProductData);
    httpBackend.expectGET('/cubesreports/cube/vw_cmm_entries/facts?cut=facility%3A%7Cperiodbegin%3A2017%2C01%2C21%7Cperiodend%3A2017%2C02%2C20').respond(200, []);
    scope.loadReport();
    httpBackend.flush();

    expect(scope.reportData.length).toBe(1);
    expect(scope.reportData[0]['drug.drug_name']).toEqual("Hidralazina25mg/5mLInjectável");
    expect(scope.reportData[0].occurred_date).toEqual("2016-01-15");
  });

  it('should redirect to lot expiry date report', function () {
    var drugCode = 'test';
    scope.reportParams = {
      endTime: '2016-11-01',
      selectedFacility: {
        id: '1',
        code: 'HF1'
      }
    };

    expect(scope.generateRedirectToExpiryDateReportURL(drugCode)).toBe('/public/pages/reports/mozambique/index.html#/lot-expiry-dates?facilityCode=HF1&date=2016-11-01&drugCode=test');
  });

  it('should export data with all the required fields', function () {
    scope.reportData = [
      {

        'cmm': 1,
        'drug_code': '01A01',
        'drug_name': 'Digoxina 0,25mg Comp',
        'estimated_months': 3,
        'expiry_date': 'Apr 2019',
        'facility.facility_code': 'F_CORE',
        'facility.facility_id': 2,
        'facility.facility_name': 'Nhongonhane (Ed.Mondl.)',
        'formatted_expiry_date': 'Apr 2019',
        'formatted_last_sync_date': '12:24 AM 18 July 2017',
        'last_sync_date': '2017-07-18T07:24:44.516902+02:00',
        'location.province_code': 'MAPUTO_PROVINCIA',
        'location.province_name': 'Maputo Prov\u00edncia',
        'location.district_code': 'MARRACUENE',
        'location.district_name': 'Marracuene',
        'occurred.month': 7.0,
        'occurred.day': 26.0,
        'occurred_date': '2017-07-26',
        'occurred.year': 2017.0,
        'soonest_expiring_loh': 40,
        'stock_card_entry_id': 11,
        'stock_status': '',
        'soh': 46,
        'vw_daily_full_soh_stock_card_entry_id': 11
      }
    ];

    scope.reportParams = {
      endTime: '15-08-2017',
      selectedProvince: {name: 'Maputo Prov\u00edncia'},
      selectedDistrict: {name: 'Marracuene'},
      selectedFacility: {name: 'Nhongonhane (Ed.Mondl.)'}
    };

    spyOn(dateFormatService,'formatDateWithDateMonthYearForString').andReturn(scope.reportParams.endTime);
    spyOn(reportExportExcelService, 'exportAsXlsx');
    scope.exportXLSX();


    var expectedHeader = {

      drugCode: 'report.header.drug.code',
      drugName: 'report.header.drug.name',
      province: 'report.header.province',
      district: 'report.header.district',
      facility: 'report.header.facility',
      quantity: 'report.header.drug.quantity',
      status: 'report.header.status',
      earliestDrugExpiryDate: 'report.header.earliest.drug.expiry.date',
      lotStockOnHand: 'report.header.lot.stock.on.hand',
      estimatedMonths: 'report.header.estimated.months',
      lastUpdateFromTablet: 'report.header.last.update.from.tablet',
      generatedFor: 'report.header.generated.for'
    };

    var date = Date.parse("2017-07-18T07:24:44.516902+02:00");
    var expectedSyncDate = dateFilter(date, 'dd/MM/yyyy HH:mm');

    var expectedContent = {
      drugCode: '01A01',
      drugName: 'Digoxina 0,25mg Comp',
      province: 'Maputo Prov\u00edncia',
      district: 'Marracuene',
      facility: 'Nhongonhane (Ed.Mondl.)',
      quantity: 46,
      status: '',
      earliestDrugExpiryDate: 'Apr 2019',
      lotStockOnHand: 40,
      estimatedMonths: 3,
      lastUpdateFromTablet: expectedSyncDate,
      generatedFor: scope.reportParams.endTime
    };

    var expectedExcel = {
      reportHeaders: expectedHeader,
      reportContent: [expectedContent]
    };

    expect(reportExportExcelService.exportAsXlsx).toHaveBeenCalledWith(expectedExcel, 'report.file.single.facility.soh.report');
  });

});