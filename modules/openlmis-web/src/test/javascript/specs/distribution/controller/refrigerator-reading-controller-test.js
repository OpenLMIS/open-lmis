describe('RefrigeratorReadingController', function () {
  var scope;

  beforeEach(inject(function ($rootScope, $controller) {
    scope = $rootScope.$new();
    $controller(RefrigeratorReadingController, {$scope: scope});
  }));

  it('should return red status class if refrigerator form is completely unfilled', function () {
    scope.refrigeratorReading = {};

    var status = scope.getStatus();

    expect(status).toEqual("is-empty");
  });

  it('should return red status class if internal refrigerator form fields are filled', function () {
    scope.refrigeratorReading = {temperature: undefined};

    var status = scope.getStatus();

    expect(status).toEqual("is-empty");
  });

  it('should return yellow status class if internal refrigerator form fields are partially filled', function () {
    scope.refrigeratorReading = {temperature: undefined, functioningCorrectly: {value: 'Y'}};

    var status = scope.getStatus();

    expect(status).toEqual("is-incomplete");
  });

  it('should return yellow status class if internal refrigerator form fields are partially filled and not recorded flag undefined',
      function () {
        scope.refrigeratorReading = {temperature: {value: 7}, functioningCorrectly: {value: 'Y'},
          lowAlarmEvents: {notRecorded: undefined}, highAlarmEvents: {value: undefined, notRecorded: undefined}, problemSinceLastTime: {value: 'Y'}};

        var status = scope.getStatus();

        expect(status).toEqual("is-incomplete");
      });

  it('should return yellow status class if internal refrigerator form fields are empty string and not recorded flag undefined',
      function () {
        scope.refrigeratorReading = {temperature: {value: 7}, functioningCorrectly: {value: 'Y'},
          lowAlarmEvents: {value: 3}, highAlarmEvents: {value: ''}, problemSinceLastTime: {value: 'Y'}};

        var status = scope.getStatus();

        expect(status).toEqual("is-incomplete");
      });

  it('should return green status class if internal refrigerator form fields are completely filled', function () {
    scope.refrigeratorReading = {temperature: {value: 7}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6}, problemSinceLastTime: {notRecorded: true}};

    var status = scope.getStatus();

    expect(status).toEqual("is-complete");
  });

  it('should return green status class if internal refrigerator form fields are completely filled or NR flag set',
      function () {
        scope.refrigeratorReading = {temperature: {notRecorded: true}, functioningCorrectly: {value: 'Y'},
          lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: undefined, notRecorded: true}};

        var status = scope.getStatus();

        expect(status).toEqual("is-complete");
      });

  it('should return yellow status class if all fields filled but not even 1 problem selected', function () {
    scope.refrigeratorReading = {temperature: {notRecorded: true}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'Y', notRecorded: false}};

    scope.refrigeratorReading.problems = {};
    var status = scope.getStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should return yellow status class if all fields filled but not even 1 problem selected', function () {
    scope.refrigeratorReading = {temperature: {notRecorded: true}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'Y', notRecorded: false}};

    scope.refrigeratorReading.problems = {problemMap: {}};
    var status = scope.getStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should return green status class if all fields filled and problems selected', function () {
    scope.refrigeratorReading = {temperature: {notRecorded: true}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'Y', notRecorded: false}};

    scope.refrigeratorReading.problems = {problemMap: {'problem': 'i had problems'}};
    var status = scope.getStatus();

    expect(status).toEqual('is-complete');
  });

  it('should return yellow status class if all fields filled and problems selected', function () {
    scope.refrigeratorReading = {temperature: {notRecorded: false}, functioningCorrectly: undefined,
      lowAlarmEvents: undefined, highAlarmEvents: undefined, problemSinceLastTime: undefined};

    var status = scope.getStatus();

    expect(status).toEqual('is-empty');
  });

  it('should return green status class if all fields filled and problems selected', function () {
    scope.refrigeratorReading = {temperature: {notRecorded: true}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'Y', notRecorded: false}};

    scope.refrigeratorReading.problems = {problemMap: {'problem': false, 'problem2': undefined}};
    var status = scope.getStatus();

    expect(status).toEqual('is-incomplete');
  });


});