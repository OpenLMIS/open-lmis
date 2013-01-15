describe('InitiateMyFacilityRnrController', function () {
  var scope, ctrl, $httpBackend, facilities, programs;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    facilities = [
      {"id": "10134", "name": "National Warehouse", "description": null}
    ];
    programs = [
      {"code": "HIV", "name": "HIV", "description": "HIV", "active": true}
    ];

    $httpBackend.expectGET('/logistics/user/facilities.json').respond(200, {"facilityList": facilities});
    ctrl = $controller(InitiateMyFacilityRnrController, {$scope: scope});
  }));

  it('should set facilities in scope', function () {
    $httpBackend.flush();

    expect(scope.$parent.facilities).toEqual(facilities);
  });

  it('should load user supported programs for selected facility for create R&R', function () {
    scope.$parent.selectedFacilityId = facilities[0].id;
    $httpBackend.expectGET('/logistics/facility/10134/user/programs.json').respond({"programList": programs});

    scope.loadPrograms();
    $httpBackend.flush();

    expect(scope.$parent.programs).toEqual(programs);
  });

  it('should not load user supported programs if there is no selected facility for create R&R', function () {
    scope.$parent.selectedFacilityId = null;

    scope.loadPrograms();
    $httpBackend.flush();

    expect(scope.$parent.programs).toEqual(null);
    expect(scope.$parent.selectedProgram).toEqual(null);
  });
});