describe('RefrigeratorController', function () {
  var scope, distribution, IndexedDB;

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

    $routeParams.facility = 1;

    distribution = {
      facilityDistributionData: {
        1: {
          refrigeratorReadings: [
            {refrigerator: {serialNumber: "abc"}},
            {refrigerator: {serialNumber: "XYZ"}}
          ]
        }
      }
    };
    $controller(RefrigeratorController, {$scope: scope, distribution: distribution, IndexedDB: IndexedDB});
  }))
  ;

  it('should set status indicator to complete if all refrigeratorReadings are complete', function () {
    scope.distribution.facilityDistributionData[1].refrigeratorReadings = [
      {status: 'is-complete'}
    ];

    var status = scope.getStatus();

    expect(status).toEqual('is-complete');
  });

  it('should set status indicator to empty if all refrigeratorReadings are empty', function () {
    scope.distribution.facilityDistributionData[1].refrigeratorReadings = [
      {status: 'is-empty'},
      {status: 'is-empty'}
    ];

    var status = scope.getStatus();

    expect(status).toEqual('is-empty');
  });

  it('should set status indicator to incomplete if at least one refrigeratorReading is incomplete', function () {
    scope.distribution.facilityDistributionData[1].refrigeratorReadings = [
      {status: 'is-incomplete'}
    ];

    var status = scope.getStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should set status indicator to incomplete if at least one refrigeratorReading is complete and rest are empty',
      function () {
        scope.distribution.facilityDistributionData[1].refrigeratorReadings = [
          {status: 'is-complete'},
          {status: 'is-complete'},
          {status: 'is-empty'}
        ];

        var status = scope.getStatus();

        expect(status).toEqual('is-incomplete');
      });

  it('should set status indicator to complete if no refrigeratorReading exists', function () {
    scope.distribution.facilityDistributionData[1].refrigeratorReadings = [];

    var status = scope.getStatus();

    expect(status).toEqual('is-complete');
  });

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

    scope.addRefrigeratorToStore()

    expect(scope.addRefrigeratorModal).toBeUndefined();
    expect(scope.isDuplicateSerialNumber).toBeUndefined();
    expect(scope.newRefrigerator).toBeUndefined();
    expect(scope.isDuplicateSerialNumber).toBeFalsy();
    expect(scope.distribution.facilityDistributionData[1].refrigeratorReadings.length).toEqual(3);
    expect(IndexedDB.put).toHaveBeenCalledWith('distributions', scope.distribution);
  });

});
