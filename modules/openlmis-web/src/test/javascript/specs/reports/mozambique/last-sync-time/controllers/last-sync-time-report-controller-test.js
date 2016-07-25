xdescribe('last sync time controller', function () {
  var scope, httpBackend;

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));

  beforeEach(inject(function ($controller, $rootScope, _$httpBackend_) {
    scope = $rootScope.$new();
    scope.provinces = [];
    scope.districts = [];
    httpBackend = _$httpBackend_;
    $controller(LastSyncTimeReportController, {$scope: scope});
  }));

  it('should load provinces and districts based on profile', function () {


    httpBackend.expectGET('/rest-api/lookup/geographic-zones').respond(200, {
      'geographic-zones': [{
        'catchmentPopulation': null,
        'code': 'MARRACUENE',
        'id': 5,
        'latitude': null,
        'levelId': 3,
        'longitude': null,
        'name': 'Marracuene',
        'parent': null,
        'parentId': 4,
        'levelCode': 'district'
      }, {
        'catchmentPopulation': null,
        'code': 'MATOLA',
        'id': 6,
        'latitude': null,
        'levelId': 3,
        'longitude': null,
        'name': 'Matola',
        'parent': null,
        'parentId': 4,
        'levelCode': 'district'
      }, {
        'catchmentPopulation': null,
        'code': 'MAPUTO_PROVINCIA',
        'id': 4,
        'latitude': null,
        'levelId': 2,
        'longitude': null,
        'name': 'Maputo Província',
        'parent': null,
        'parentId': 3,
        'levelCode': 'province'
      }, {
        'catchmentPopulation': null,
        'code': 'MOZ',
        'id': 3,
        'latitude': null,
        'levelId': 1,
        'longitude': null,
        'name': 'Mozambique',
        'parent': null,
        'parentId': null,
        'levelCode': 'national'
      }]
    });

    scope.$broadcast('$viewContentLoaded');
    httpBackend.flush();

    expect(scope.provinces).toEqual([
      {
        catchmentPopulation: null,
        code: 'MAPUTO_PROVINCIA',
        id: 4,
        latitude: null,
        levelId: 2,
        longitude: null,
        name: 'Maputo Província',
        parent: null,
        parentId: 3,
        levelCode: 'province'
      }
    ]);

    expect(scope.districts).toEqual([{
      catchmentPopulation: null,
      code: 'MARRACUENE',
      id: 5,
      latitude: null,
      levelId: 3,
      longitude: null,
      name: 'Marracuene',
      parent: null,
      parentId: 4,
      levelCode: 'district'
    }, {
      catchmentPopulation: null,
      code: 'MATOLA',
      id: 6,
      latitude: null,
      levelId: 3,
      longitude: null,
      name: 'Matola',
      parent: null,
      parentId: 4,
      levelCode: 'district'
    }])
  });
});