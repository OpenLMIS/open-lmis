describe('OvrerStockReportController', function () {
  var scope, httpBackend, messageService, overStockProductsService;

  var overStockList = {
    'data': [
      {

        provinceId: 61,
        provinceName: 'Province',
        districtId: 63,
        districtName: 'District',
        facilityId: 570,
        facilityName: 'Facility one',
        productId: 216,
        productCode: '00001',
        productName: 'Product one',
        lotList: [
          {
            lotNumber: '00001',
            expiryDate: 1546207200000,
            stockOnHandOfLot: 0
          },
          {
            lotNumber: '00002',
            expiryDate: 1546207200000,
            stockOnHandOfLot: 30
          }
        ],
        cmm: 610,
        mos: 4.590163934426229,
        isHiv: false
      }
    ]
  };

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));
  beforeEach(inject(function (_$httpBackend_, $rootScope, $controller, _messageService_, ReportExportExcelService, OverStockProductsService) {
    scope = $rootScope.$new();
    httpBackend = _$httpBackend_;
    messageService = _messageService_;
    overStockProductsService = OverStockProductsService;
    $controller(OverStockReportController, {$scope: scope});
  }));

  it('should load facility level over stock product list successfully', function () {
    scope.reportParams.endTime = '2017-02-01';
    scope.reportParams.facilityId = 1;
    scope.reportParams.districtId = 1;
    scope.reportParams.provinceId = 1;
    
    var params = {
      provinceId: '1',
      districtId: '1',
      facilityId: '1',
      endTime: '2017-02-01 23:59:59',
      reportType: 'overStockProductsReport'
    };
    
    httpBackend.expectPOST('/reports/data', params).respond(200, overStockList);

    scope.loadReport();
    httpBackend.flush();

    expect(scope.showOverStockProductsTable).toBeTruthy();
    expect(scope.formattedOverStockList.length).toBe(2);

    expect(scope.formattedOverStockList[0].provinceName).toEqual('Province');
    expect(scope.formattedOverStockList[0].districtName).toEqual('District');
    expect(scope.formattedOverStockList[0].facilityName).toEqual('Facility one');
    expect(scope.formattedOverStockList[0].productCode).toEqual('00001');
    expect(scope.formattedOverStockList[0].productName).toEqual('Product one');
    expect(scope.formattedOverStockList[0].lotNumber).toEqual('00001');
    expect(scope.formattedOverStockList[0].stockOnHandOfLot).toEqual(0);
    expect(scope.formattedOverStockList[0].cmm).toEqual(610);
    expect(scope.formattedOverStockList[0].mos).toEqual(4.59);
    expect(scope.formattedOverStockList[0].isFirst).toBeTruthy();
    expect(scope.formattedOverStockList[0].rowSpan).toEqual(2);

    expect(scope.formattedOverStockList[1].lotNumber).toEqual('00002');
    expect(scope.formattedOverStockList[1].stockOnHandOfLot).toEqual(30);
    expect(scope.formattedOverStockList[1].isFirst).toBeFalsy();
  });

  it('should call exportXLSX for OverStockProductsService', function () {
    spyOn(overStockProductsService, 'getDataForExport');
    scope.reportParams.endTime = '2017-02-01';
    scope.reportParams.facilityId = 1;
    scope.reportParams.districtId = 1;
    scope.reportParams.provinceId = 1;
    scope.exportXLSX();

    expect(overStockProductsService.getDataForExport).toHaveBeenCalled();
  });
});