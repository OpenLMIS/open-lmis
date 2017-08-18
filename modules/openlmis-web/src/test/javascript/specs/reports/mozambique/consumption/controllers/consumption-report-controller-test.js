describe("consumption report controller", function () {
  var scope, reportExportExcelService;

  function getExpectedHeaders() {
    var expectedHeader = {
      drugCode: 'report.header.drug.code',
      drugName: 'report.header.drug.name',
      province: 'report.header.province',
      district: 'report.header.district',
      facility: 'report.header.facility',
      period: 'report.header.period',
      cmm: 'report.header.cmm',
      consumption: 'report.header.consumption.during.period',
      soh: 'report.header.soh.at.period.end',
      reportGeneratedFor: 'report.header.generated.for'
    };
    return expectedHeader;
  }

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));

  beforeEach(inject(function ($controller, $rootScope, ReportExportExcelService) {
    scope = $rootScope.$new();
    reportExportExcelService = ReportExportExcelService
    $controller(ConsumptionReportController, {$scope: scope});
  }));

  it('should split for each period in selected range', function () {
    var periods = scope.splitPeriods("2016-01-01T00:00:00", "2016-04-21T00:00:00");

    expect(periods.length).toBe(5);
    expect(periods).toEqual([
      {periodStart: new Date("2015-12-21T00:00:00"), periodEnd: new Date("2016-01-20T00:00:00")},
      {periodStart: new Date("2016-01-21T00:00:00"), periodEnd: new Date("2016-02-20T00:00:00")},
      {periodStart: new Date("2016-02-21T00:00:00"), periodEnd: new Date("2016-03-20T00:00:00")},
      {periodStart: new Date("2016-03-21T00:00:00"), periodEnd: new Date("2016-04-20T00:00:00")},
      {periodStart: new Date("2016-04-21T00:00:00"), periodEnd: new Date("2016-05-20T00:00:00")}
    ]);
  });

  it('should only split one period when selected range in one period', function () {
    var periods = scope.splitPeriods("2016-01-01T00:00:00", "2016-01-19T00:00:00");

    expect(periods.length).toBe(1);
    expect(periods).toEqual([
      {periodStart: new Date("2015-12-21T00:00:00"), periodEnd: new Date("2016-01-20T00:00:00")}
    ]);
  });

  it('should export all the date from report', function () {
    var expectedHeader = getExpectedHeaders();

    scope.consumptionInPeriods = [{
      cmm: null,
      cmm_sum: null,
      occurrences: 0,
      period: 'Mar 2017-Apr 2017',
      soh: 4,
      soh_sum: 4,
      total_quantity: 0,
      total_quantity_sum: 0
    }];

    scope.reportParams = {
      startTime: '2017-04-21T00:00:00.000000',
      endTime: '2017-07-20T00:00:00.000000',
      selectedProvince: {name: 'Maputo Província'},
      productCode: '08S01Z'
    };

    spyOn(reportExportExcelService, 'exportAsXlsx');
    spyOn(scope, 'getDrugByCode').andReturn({code:'08S01Z', primaryName:'Paracetamol120mg/5mLXarope'});
    scope.exportXLSX();

    var expectedContent = {
      drugCode: '08S01Z',
      drugName: 'Paracetamol120mg/5mLXarope',
      province: 'Maputo Província',
      district: '[All]',
      facility: '[All]',
      period: 'Mar 2017-Apr 2017',
      cmm: '',
      consumption: 0,
      soh: 4,
      reportGeneratedFor: '21-04-2017 - 20-07-2017'
    };

    var expectedExcel = {
      reportHeaders: expectedHeader,
      reportContent: [expectedContent]
    };

    expect(reportExportExcelService.exportAsXlsx).toHaveBeenCalledWith(expectedExcel, 'report.file.historical.data.report');
  });
});