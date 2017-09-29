describe("Lot Expiry Dates Report Controller", function () {
  var scope, httpBackend, reportExportExcelService, dateFilter;

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));
  beforeEach(inject(function (_$httpBackend_, $rootScope, $filter, $controller, ReportExportExcelService) {
    scope = $rootScope.$new();
    dateFilter = $filter('date');
    httpBackend = _$httpBackend_;
    reportExportExcelService = ReportExportExcelService;
    $controller(LotExpiryDatesReportController, {$scope: scope});
  }));

  it('should export data with district province name successfully', function () {

    var date = Date.parse("2017-02-28T00:00:00.00000+02:00");
    var expiryDate = dateFilter(date, 'dd/MM/yyyy HH:mm');

    var lot_expiry_date = {
      expiry_date: expiryDate,
      formatted_expiry_date: 'Feb 2017',
      lot_expiry: 'ZZZ - Feb 2017',
      lot_number: 'ZZZ',
      lot_on_hand: '123'
    };

    var another_lot_expiry_date = {
      expiry_date: expiryDate,
      formatted_expiry_date: 'Feb 2017',
      lot_expiry: 'EEE - Feb 2017',
      lot_number: 'EEE',
      lot_on_hand: '150'
    };

    scope.expiryDatesReportParams = {
      endTime: 1502945999999
    };


    scope.reportData = [
      {
        'code': '08S42B',
        'location.province_code': 'MAPUTO_PROVINCIA',
        'name': 'Zidovudina/Lamivudina/Nevirapi; 60mg+30mg+50mg 60 Comprimidos; Embalagem',
        'province_name': 'Maputo Prov\u00edncia',
        'facility_name': 'Nhongonhane (Ed.Mondl.)',
        'lot_number': 'ZZZ',
        'location.district_code': 'MARRACUENE',
        'lotonhand': 123,
        'occurred': 1501459200000.0,
        'facility.facility_code': 'F_CORE',
        'stock_card_entry_id': 18,
        'expiry_date': '2017-02-28T00:00:00.00000+02:00',
        'createddate': 1501538863000.0,
        'district_name': 'Marracuene',
        'lot_expiry_dates': [lot_expiry_date]
      },
      {
        'code': '08S32Z',
        'location.province_code': 'MAPUTO_PROVINCIA',
        'name': 'Estavudina/Lamivudina; 6mg+30mg, 60 Comp (Baby); Embalagem',
        'province_name': 'Maputo Prov\u00edncia',
        'facility_name': 'Nhongonhane (Ed.Mondl.)',
        'lot_number': 'EEE',
        'location.district_code': 'MARRACUENE',
        'lotonhand': 150,
        'occurred': 1501459200000.0,
        'facility.facility_code': 'F_CORE',
        'stock_card_entry_id': 25,
        'expiry_date': '2017-02-28T00:00:00.00000+02:00',
        'createddate': 1501538890000.0,
        'district_name': 'Marracuene',
        'lot_expiry_dates': [another_lot_expiry_date]
      }
    ];


    spyOn(reportExportExcelService, 'exportAsXlsx');
    scope.exportXLSX();

    var expectedHeader = {
      drugCode: 'report.header.drug.code',
      drugName: 'report.header.drug.name',
      province: 'report.header.province',
      district: 'report.header.district',
      facility: 'report.header.facility',
      lot: 'report.header.lot',
      expiryDate: 'report.header.expiry.date',
      soh: 'report.header.stock.on.hand',
      reportGenerateDate: 'report.header.generated.for'
    };

    var expectedContent = [
      {
        drugCode: '08S42B',
        drugName: 'Zidovudina/Lamivudina/Nevirapi; 60mg+30mg+50mg 60 Comprimidos; Embalagem',
        province: 'Maputo Província',
        district: 'Marracuene',
        facility: 'Nhongonhane (Ed.Mondl.)',
        lot: 'ZZZ',
        expiryDate: 'Feb 2017',
        soh: '123',
        reportGenerateDate: '17-08-2017'
      },
      {
        drugCode: '08S32Z',
        drugName: 'Estavudina/Lamivudina; 6mg+30mg, 60 Comp (Baby); Embalagem',
        province: 'Maputo Província',
        district: 'Marracuene',
        facility: 'Nhongonhane (Ed.Mondl.)',
        lot: 'EEE',
        expiryDate: 'Feb 2017',
        soh: '150',
        reportGenerateDate: '17-08-2017'
      }
    ];
    var expectedExcel = {
      reportHeaders: expectedHeader,
      reportContent: expectedContent
    };

    expect(reportExportExcelService.exportAsXlsx).toHaveBeenCalledWith(expectedExcel, 'report.file.expiry.dates.report');
  });
});