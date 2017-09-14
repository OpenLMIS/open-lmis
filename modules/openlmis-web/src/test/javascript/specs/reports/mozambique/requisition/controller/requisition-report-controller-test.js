describe('requisition report controller', function () {
  var scope, httpBackend, messageService, reportExportExcelService, dateFormatService;

  var requisitions = {
    'rnr_list': [
      {
        'id': 148,
        'programName': 'VIA',
        'type': 'Emergency',
        'emergency': true,
        'facilityName': 'Matalane',
        'districtName': 'Marracuene',
        'provinceName': 'Maputo',
        'submittedUser': 'mystique',
        'clientSubmittedTimeString': '2016-10-27 11:11:20',
        'actualPeriodEnd': null,
        'schedulePeriodEnd': 1463759999000,
        'webSubmittedTime': 1463453167471,
        'clientSubmittedTime': 1477537880000,
        'requisitionStatus': 'AUTHORIZED'
      },
      {
        'id': 149,
        'programName': 'VIA',
        'type': 'Normal',
        'emergency': false,
        'facilityName': 'Matalane',
        'districtName': 'Marracuene',
        'provinceName': 'Maputo',
        'submittedUser': 'mystique',
        'clientSubmittedTimeString': '2016-05-20 23:59:59',
        'actualPeriodEnd': 1456197080000,
        'schedulePeriodEnd': 1463759999000,
        'webSubmittedTime': 1463453174780,
        'clientSubmittedTime': 1463759999000,
        'requisitionStatus': 'AUTHORIZED'
      },
      {
        'id': 150,
        'programName': 'VIA',
        'type': 'Normal',
        'emergency': false,
        'facilityName': 'Matalane',
        'districtName': 'Marracuene',
        'provinceName': 'Maputo',
        'submittedUser': 'mystique',
        'clientSubmittedTimeString': '',
        'actualPeriodEnd': 1456197080000,
        'schedulePeriodEnd': 1463759999000,
        'webSubmittedTime': 1463453174780,
        'clientSubmittedTime': null,
        'requisitionStatus': 'AUTHORIZED'
      }
    ]
  };

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));
  beforeEach(inject(function (_$httpBackend_, $rootScope, $controller, _messageService_, ReportExportExcelService, DateFormatService) {
    scope = $rootScope.$new();
    httpBackend = _$httpBackend_;
    messageService = _messageService_;
    reportExportExcelService = ReportExportExcelService;
    dateFormatService = DateFormatService;
    $controller(RequisitionReportController, {$scope: scope});
  }));


  it('should load facility and stock movements successfully', function () {
    scope.reportParams.startTime = '2017-01-01';
    scope.reportParams.endTime = '2017-02-01';
    scope.reportParams.facilityId = 1;
    scope.reportParams.districtId = 1;
    scope.reportParams.provinceId = 1;
    scope.reportParams.selectedFacility = {'name': 'Matalane'};
    scope.reportParams.selectedDistrict = {'name': 'Marracuene'};
    scope.reportParams.selectedProvince = {'name': 'Maputo'};
    httpBackend.expectGET('/reports/requisition-report.json?endTime=2017-02-01+23:59:59&startTime=2017-01-01+00:00:00').respond(200, requisitions);

    scope.loadReport();
    httpBackend.flush();

    expect(scope.requisitions.length).toBe(3);

    expect(scope.requisitions[0].actualPeriodEnd).toBe(1463759999000);
    expect(scope.requisitions[0].submittedStatus).toBe(messageService.get('rnr.report.submitted.status.late'));
    expect(scope.requisitions[1].actualPeriodEnd).toBe(1456197080000);
    expect(scope.requisitions[1].submittedStatus).toBe(messageService.get('rnr.report.submitted.status.ontime'));
    expect(scope.requisitions[2].submittedStatus).toBe(undefined);
  });

  it('should get redirect url of rnr detail page', function () {
    scope.selectedItems[0] = {
      'id': 150,
      'programName': 'VIA'
    };

    expect(scope.getRedirectUrl()).toBe('/public/pages/logistics/rnr/index.html#/view-requisition-via/150?supplyType=fullSupply&page=1');
  });

  function getExpectedHeaders() {
    var expectedHeader = {
      programName: 'report.header.program.name',
      type: 'report.header.type',
      provinceName: 'report.header.province',
      districtName: 'report.header.district',
      facilityName: 'report.header.facility.name',
      submittedUser: 'report.header.submitted.user',
      inventoryDate: 'report.header.inventory.date',
      submittedStatus: 'report.header.submitted.status',
      submittedTime: 'report.header.submitted.time',
      syncTime: 'report.header.sync.time',
      reportGeneratedFor: 'report.header.generated.for'
    };
    return expectedHeader;
  }
  
  it('should export data with district province name successfully', function () {
    var expectedHeader = getExpectedHeaders();
    scope.requisitions = [{
      actualPeriodEnd: 1500028406115,
      clientSubmittedTime: 1500029935097,
      clientSubmittedTimeString: '2017-07-14 05:58:55',
      districtName: 'Moamba',
      emergency: false,
      facilityName: 'Sabie',
      id: 727,
      inventoryDate: '14 July 2017',
      programName: 'VIA Classica',
      provinceName: 'Maputo Província',
      requisitionStatus: 'AUTHORIZED',
      schedulePeriodEnd: 1500526800000,
      submittedStatus: 'On time',
      submittedUser: 'Sabie',
      type: 'Normal',
      webSubmittedTime: 1500029942231,
      webSubmittedTimeString: '2017-07-14 05:59:02'
    }];

    scope.reportParams = {
      startTime: '21-07-2017',
      endTime: '21-10-2017'
    };

    spyOn(dateFormatService,'formatDateWithDateMonthYearForString').andCallFake(function(arg) {return arg;});
    spyOn(reportExportExcelService, 'exportAsXlsx');
    scope.exportXLSX();

    var expectedContent = {
      programName: 'VIA Classica',
      type: 'Normal',
      provinceName: 'Maputo Província',
      districtName: 'Moamba',
      facilityName: 'Sabie',
      submittedUser: 'Sabie',
      inventoryDate: '14 July 2017',
      submittedStatus: 'On time',
      submittedTime: '2017-07-14 05:58:55',
      syncTime: '2017-07-14 05:59:02',
      reportGeneratedFor: scope.reportParams.startTime + ' - ' + scope.reportParams.endTime
    };

    var expectedExcel = {
      reportHeaders: expectedHeader,
      reportContent: [expectedContent]
    };

    expect(reportExportExcelService.exportAsXlsx).toHaveBeenCalledWith(expectedExcel, 'report.file.requisition.report');

  });
});