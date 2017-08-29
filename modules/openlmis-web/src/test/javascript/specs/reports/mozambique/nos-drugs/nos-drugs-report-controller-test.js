describe("nos drugs chart service test", function () {
  var nosDrugsChartService, scope;

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));
  beforeEach(inject(function (NosDrugsChartService, $rootScope, $controller) {
    nosDrugsChartService = NosDrugsChartService;
    scope = $rootScope.$new();
    $controller(NosDrugsReportController, {$scope: scope});
    scope.provinces = [{provinceId: 1, name: 'province1'}, {provinceId: 2, name: 'province2'}];
    scope.districts = [{districtId: 1, name: 'district1'}, {districtId: 2, name: 'district2'}];
    scope.reportParams = {
      provinceId: 1,
      districtId: 2,
      startTime: '01/01/2017',
      endTime: '01/08/2017'
    }
  }));


  it('should call nos drugs chart service exportXLSX', function () {
    spyOn(scope, 'getGeographicZoneById').andCallFake(function (param1, param2) {
      return param1[0].name;
    });
    spyOn(nosDrugsChartService, 'exportXLSX');
    scope.exportXLSX();
    expect(nosDrugsChartService.exportXLSX).toHaveBeenCalledWith(scope.reportParams.startTime, scope.reportParams.endTime,
      scope.provinces[0].name, scope.districts[0].name);
  });

  it('should call load report method', function () {
    spyOn(scope, 'validateProvince').andReturn(true);
    spyOn(scope, 'validateDistrict').andReturn(true);
    spyOn(scope, 'getGeographicZoneById').andCallFake(function (param1, param2) {
      return param1[0].name;
    });
    spyOn(nosDrugsChartService, 'makeNosDrugsChart');
    scope.loadReport();
    expect(nosDrugsChartService.makeNosDrugsChart).toHaveBeenCalledWith('tracer-report', 'legend-div',
      new Date(scope.reportParams.startTime), new Date(scope.reportParams.endTime),
      scope.provinces[0].name, scope.districts[0].name);
  });

});