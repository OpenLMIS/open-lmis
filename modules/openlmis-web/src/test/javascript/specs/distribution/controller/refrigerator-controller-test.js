describe('RefrigeratorController', function () {
  var scope;

  beforeEach(module('distribution'));
  beforeEach(inject(function ($rootScope, $controller, $routeParams) {
    var IndexedDB = {
      execute: function () {
      },
      get: function () {

      },
      put: function () {

      }
    };

    scope = $rootScope.$new();

    $routeParams.facility = 1;

    $controller(RefrigeratorController, {$scope: scope, distribution: {facilityDistributionData: {1: {}}}, IndexedDB: IndexedDB});
  }));

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
});
