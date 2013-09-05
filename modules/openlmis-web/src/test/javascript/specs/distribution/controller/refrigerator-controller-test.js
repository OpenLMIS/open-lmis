describe('RefrigeratorController', function () {
  var scope, distributionService, IndexedDB, distribution;

  beforeEach(module('distribution'));
  beforeEach(inject(function ($rootScope, $controller, $routeParams) {
    IndexedDB = {
      execute: function () {
      },
      get: function () {

      },
      put: function () {

      }
    };

    scope = $rootScope.$new();

    distributionService = {};

    distribution = new Distribution({
      facilityDistributionData: {
        1: { refrigerators: {
          refrigeratorReadings: [
            {refrigerator: {serialNumber: "abc"}},
            {refrigerator: {serialNumber: "XYZ"}}
          ]
        }
        }
      }
    });

    distributionService.distribution = distribution;

    $routeParams.facility = 1;
    $controller(RefrigeratorController, {$scope: scope, IndexedDB: IndexedDB, distributionService: distributionService});
  }))
  ;

  it('should initialize controller', function () {
    expect(scope.distribution).toEqual(distribution);
    expect(scope.selectedFacilityId).toEqual(1);
  });

  it('should set edit for specific serial number', function () {
    scope.edit = [
      {'key1': true, 'key2': false}
    ];
    scope.setEdit('key2');
    expect(scope.edit.key1).toBeFalsy();
    expect(scope.edit.key2).toBeTruthy();
  });

  it('should set duplicate serial number if serial number already exists', function () {
    scope.newRefrigerator = {serialNumber: "Abc"};
    scope.addRefrigeratorToStore()
    expect(scope.isDuplicateSerialNumber).toBeTruthy();
  });

  it('should add new refrigerator if serial number does not exist', function () {
    scope.newRefrigerator = {serialNumber: "Abcc"};
    spyOn(IndexedDB, 'put').andCallThrough();

    scope.addRefrigeratorToStore();

    expect(scope.addRefrigeratorModal).toBeUndefined();
    expect(scope.isDuplicateSerialNumber).toBeUndefined();
    expect(scope.isDuplicateSerialNumber).toBeFalsy();
    expect(scope.distribution.facilityDistributionData[1].refrigerators.refrigeratorReadings.length).toEqual(3);
    expect(IndexedDB.put).toHaveBeenCalledWith('distributions', scope.distribution);
  });

});
