describe('RefrigeratorReading', function () {
  var refrigeratorReading;

  it('should return red status class if refrigerator form is completely unfilled', function () {
    refrigeratorReading = new RefrigeratorReading({});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual("is-empty");
  });

  it('should return red status class if internal refrigerator form fields are filled', function () {
    refrigeratorReading = new RefrigeratorReading({temperature: undefined});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual("is-empty");
  });

  it('should return yellow status class if internal refrigerator form fields are partially filled', function () {
    refrigeratorReading = new RefrigeratorReading({temperature: undefined, functioningCorrectly: {value: 'Y'}});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual("is-incomplete");
  });

  it('should return yellow status class if internal refrigerator form fields are partially filled and not recorded flag undefined',
    function () {
      refrigeratorReading = new RefrigeratorReading({temperature: {value: 7}, functioningCorrectly: {value: 'Y'},
        lowAlarmEvents: {notRecorded: undefined}, highAlarmEvents: {value: undefined, notRecorded: undefined}, problemSinceLastTime: {value: 'Y'}});

      var status = refrigeratorReading.computeStatus();

      expect(status).toEqual("is-incomplete");
    });

  it('should return yellow status class if internal refrigerator form fields are empty string and not recorded flag undefined',
    function () {
      refrigeratorReading = new RefrigeratorReading({temperature: {value: 7}, functioningCorrectly: {value: 'Y'},
        lowAlarmEvents: {value: 3}, highAlarmEvents: {value: ''}, problemSinceLastTime: {value: 'Y'}});

      var status = refrigeratorReading.computeStatus();

      expect(status).toEqual("is-incomplete");
    });

  it('should return green status class if internal refrigerator form fields are completely filled', function () {
    refrigeratorReading = new RefrigeratorReading({temperature: {value: 7}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6}, problemSinceLastTime: {notRecorded: true}});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual("is-complete");
  });

  it('should return green status class if internal refrigerator form fields are completely filled or NR flag set',
    function () {
      refrigeratorReading = new RefrigeratorReading({temperature: {notRecorded: true}, functioningCorrectly: {value: 'Y'},
        lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: undefined, notRecorded: true}});

      var status = refrigeratorReading.computeStatus();

      expect(status).toEqual("is-complete");
    });

  it('should return yellow status class if all fields filled but not even 1 problem selected', function () {
    refrigeratorReading = new RefrigeratorReading({temperature: {notRecorded: true}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'Y', notRecorded: false}});

    refrigeratorReading.problems = {};
    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should return yellow status class if all fields filled but not even 1 problem selected', function () {
    refrigeratorReading = new RefrigeratorReading({temperature: {notRecorded: true}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'Y', notRecorded: false}});

    refrigeratorReading.problems = {problemMap: {}};
    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should return green status class if all fields filled and problems selected', function () {
    refrigeratorReading = new RefrigeratorReading({temperature: {notRecorded: true}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'Y', notRecorded: false}});

    refrigeratorReading.problems = {problemMap: {'problem': 'i had problems'}};
    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual('is-complete');
  });

  it('should return yellow status class if all fields filled and problems selected', function () {
    refrigeratorReading = new RefrigeratorReading({temperature: {notRecorded: false}, functioningCorrectly: undefined,
      lowAlarmEvents: undefined, highAlarmEvents: undefined, problemSinceLastTime: undefined});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual('is-empty');
  });

  it('should return green status class if all fields filled and problems selected', function () {
    refrigeratorReading = new RefrigeratorReading({temperature: {notRecorded: true}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'Y', notRecorded: false}});

    refrigeratorReading.problems = {problemMap: {'problem': false, 'problem2': undefined}};
    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual('is-incomplete');
  });


});