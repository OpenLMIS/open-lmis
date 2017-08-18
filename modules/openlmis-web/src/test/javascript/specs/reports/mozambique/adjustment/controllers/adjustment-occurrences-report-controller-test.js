describe("adjustment occurrences report controller", function () {
  var scope;
  var period;
  var reportExportExcelService;

  function getExpectedHeaders() {
    var expectedHeader = {
      drugCode: 'report.header.drug.code',
      drugName: 'report.header.drug.name',
      province: 'report.header.province',
      district: 'report.header.district',
      facility: 'report.header.facility',
      adjustmentType: 'report.header.adjustment.type',
      adjustmentReason: 'report.header.adjustment.reason',
      occurrencesPeriod: 'report.header.occurrences.period',
      occurrencesTimes: 'report.header.occurrences.times',
      reportGeneratedFor: 'report.header.generated.for'
    };
    return expectedHeader;
  }

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));

  beforeEach(inject(function ($controller, $rootScope,ReportExportExcelService) {
    scope = $rootScope.$new();
    $controller(AdjustmentOccurrencesReportController, {$scope: scope});
    reportExportExcelService = ReportExportExcelService;
    period = {
      periodStart: new Date("2016-05-21T00:00:00.000Z"),
      periodEnd: new Date("2016-06-20T00:00:00.000Z")
    };
  }));

  it('should generate cut params with negative reason codes when adjustment type is negative', function () {
    var adjustmentType = "negative";
    var cutParams = scope.generateCutParamsByAdjustmentType(period, adjustmentType);

    expect(cutParams).toContain({
      "dimension": "reason_code",
      "values": [
        "EXPIRED_RETURN_TO_SUPPLIER",
        "DAMAGED",
        "LOANS_DEPOSIT",
        "INVENTORY_NEGATIVE",
        "PROD_DEFECTIVE",
        "RETURN_TO_DDM"
      ]
    });
  });

  it('should generate cut params with positive reason codes when adjustment type is positive', function () {
    var adjustmentType = "positive";
    var cutParams = scope.generateCutParamsByAdjustmentType(period, adjustmentType);

    expect(cutParams).toContain({
      "dimension": "reason_code",
      "values": [
        "CUSTOMER_RETURN",
        "EXPIRED_RETURN_FROM_CUSTOMER",
        "DONATION",
        "LOANS_RECEIVED",
        "INVENTORY_POSITIVE",
        "RETURN_FROM_QUARANTINE"
      ]
    });
  });

  it('should export all the date from report', function () {
    var expectedHeader = getExpectedHeaders();

    scope.adjustmentsData = [{
      CUSTOMER_RETURN: 0,
      DAMAGED: 0,
      DONATION: 0,
      EXPIRED_RETURN_FROM_CUSTOMER:0,
      EXPIRED_RETURN_TO_SUPPLIER: 0,
      INVENTORY_NEGATIVE: 0,
      INVENTORY_POSITIVE: 0,
      LOANS_DEPOSIT: 0,
      LOANS_RECEIVED: 0,
      PROD_DEFECTIVE: 0,
      RETURN_FROM_QUARANTINE: 0,
      RETURN_TO_DDM: 0,
      period: "Apr 2017-May 2017"
    }];

    scope.reportParams = {
      startTime: '2017-04-20T00:00:00.000000',
      endTime: '2017-07-19T00:00:00.000000',
      selectedProvince: {name: 'Maputo Província'},
      productCode: '08S01Z',
      adjustmentType:'negative'
    };

    spyOn(reportExportExcelService, 'exportAsXlsx');
    spyOn(scope, 'getDrugByCode').andReturn({code:'08S01Z', primaryName:'Paracetamol120mg/5mLXarope'});
    scope.exportXLSX();

    var expectedContent = [
      {
        drugCode: '08S01Z',
        drugName: 'Paracetamol120mg/5mLXarope',
        province: 'Maputo Província',
        district: '[All]',
        facility: '[All]',
        adjustmentType: 'stock.movement.negative.adjustment',
        adjustmentReason: 'stock.movement.EXPIRED_RETURN_TO_SUPPLIER',
        occurrencesPeriod: 'Apr 2017-May 2017',
        occurrencesTimes: 0,
        reportGeneratedFor: '20-04-2017 - 19-07-2017'
      },
      {
        drugCode: '08S01Z',
        drugName: 'Paracetamol120mg/5mLXarope',
        province: 'Maputo Província',
        district: '[All]',
        facility: '[All]',
        adjustmentType: 'stock.movement.negative.adjustment',
        adjustmentReason: 'stock.movement.DAMAGED',
        occurrencesPeriod: 'Apr 2017-May 2017',
        occurrencesTimes: 0,
        reportGeneratedFor: '20-04-2017 - 19-07-2017'
      },
      {
        drugCode: '08S01Z',
        drugName: 'Paracetamol120mg/5mLXarope',
        province: 'Maputo Província',
        district: '[All]',
        facility: '[All]',
        adjustmentType: 'stock.movement.negative.adjustment',
        adjustmentReason: 'stock.movement.LOANS_DEPOSIT',
        occurrencesPeriod: 'Apr 2017-May 2017',
        occurrencesTimes: 0,
        reportGeneratedFor: '20-04-2017 - 19-07-2017'
      },
      {
        drugCode: '08S01Z',
        drugName: 'Paracetamol120mg/5mLXarope',
        province: 'Maputo Província',
        district: '[All]',
        facility: '[All]',
        adjustmentType: 'stock.movement.negative.adjustment',
        adjustmentReason: 'stock.movement.INVENTORY_NEGATIVE',
        occurrencesPeriod: 'Apr 2017-May 2017',
        occurrencesTimes: 0,
        reportGeneratedFor: '20-04-2017 - 19-07-2017'
      },
      {
        drugCode: '08S01Z',
        drugName: 'Paracetamol120mg/5mLXarope',
        province: 'Maputo Província',
        district: '[All]',
        facility: '[All]',
        adjustmentType: 'stock.movement.negative.adjustment',
        adjustmentReason: 'stock.movement.PROD_DEFECTIVE',
        occurrencesPeriod: 'Apr 2017-May 2017',
        occurrencesTimes: 0,
        reportGeneratedFor: '20-04-2017 - 19-07-2017'
      },
      {
        drugCode: '08S01Z',
        drugName: 'Paracetamol120mg/5mLXarope',
        province: 'Maputo Província',
        district: '[All]',
        facility: '[All]',
        adjustmentType: 'stock.movement.negative.adjustment',
        adjustmentReason: 'stock.movement.RETURN_TO_DDM',
        occurrencesPeriod: 'Apr 2017-May 2017',
        occurrencesTimes: 0,
        reportGeneratedFor: '20-04-2017 - 19-07-2017'
      }
    ];

    var expectedExcel = {
      reportHeaders: expectedHeader,
      reportContent: expectedContent
    };

    expect(reportExportExcelService.exportAsXlsx).toHaveBeenCalledWith(expectedExcel, 'report.file.adjustment.occurrences.report');
  });

});