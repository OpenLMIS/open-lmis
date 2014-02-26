/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('RefrigeratorReading', function () {
  var refrigeratorReading;
  var facilityVisitId = 1;
  it('should return red status class if refrigerator form is completely unfilled', function () {
    refrigeratorReading = new RefrigeratorReading({});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });

  it('should return red status class if internal refrigerator form fields are filled', function () {
    refrigeratorReading = new RefrigeratorReading({temperature: undefined});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });

  it('should return yellow status class if internal refrigerator form fields are partially filled', function () {
    refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: undefined, functioningCorrectly: {value: 'Y'}});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should return yellow status class if internal refrigerator form fields are partially filled and not recorded flag undefined',
    function () {
      refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: {value: 7}, functioningCorrectly: {value: 'N'},
        lowAlarmEvents: {notRecorded: undefined}, highAlarmEvents: {value: undefined, notRecorded: undefined}, problemSinceLastTime: {value: 'Y'}});

      var status = refrigeratorReading.computeStatus();

      expect(status).toEqual(DistributionStatus.INCOMPLETE);
    });

  it('should return yellow status class if internal refrigerator form fields are empty string and not recorded flag undefined',
    function () {
      refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: {value: 7}, functioningCorrectly: {value: 'N'},
        lowAlarmEvents: {value: 3}, highAlarmEvents: {value: ''}, problemSinceLastTime: {value: 'Y'}});

      var status = refrigeratorReading.computeStatus();

      expect(status).toEqual(DistributionStatus.INCOMPLETE);
    });

  it('should return green status class if internal refrigerator form fields are completely filled', function () {
    refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: {value: 7}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6}, problemSinceLastTime: {notRecorded: true}});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should return green status class if internal refrigerator form fields are completely filled', function () {
    refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: {value: 7}, functioningCorrectly: {value: 'Y'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6}, problemSinceLastTime: {notRecorded: true}});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should return green status class if internal refrigerator form fields are completely filled, including notes, or NR flag set',
    function () {
      refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: {notRecorded: true}, functioningCorrectly: {value: 'Y'},
        lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'Y'}, problems: {other: true, otherProblemExplanation: 'eer'}});

      var status = refrigeratorReading.computeStatus();

      expect(status).toEqual(DistributionStatus.COMPLETE);
    });

  it('should return yellow status class if all fields filled but not even 1 problem selected', function () {
    refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: {notRecorded: true}, functioningCorrectly: {value: 'N'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'N', notRecorded: false}});


    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should return green status class if all fields filled and problems selected', function () {
    refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: {notRecorded: true}, functioningCorrectly: {value: 'N'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'N'}});

    refrigeratorReading.problems = {operatorError: true};
    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should return red status class if all fields are not filled and no problems selected', function () {
    refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: {notRecorded: false}, functioningCorrectly: undefined,
      lowAlarmEvents: undefined, highAlarmEvents: undefined, problemSinceLastTime: undefined});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });

  it('should return yellow status class if some fields are filled and some problems selected', function () {
    refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: {notRecorded: true}, functioningCorrectly: {value: 'N'},
      lowAlarmEvents: {value: 3}, highAlarmEvents: {value: 6, notRecorded: true}, problemSinceLastTime: {value: 'Y', notRecorded: false}});

    refrigeratorReading.problems = {problemMap: {'problem': false, 'problem2': undefined}};
    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should return red status class if no fields are filled and temperature if filled with negative sign', function () {
    refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: {value: "-"}});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });

  it('should return red status class if no fields are filled and temperature if filled with decimal sign', function () {
    refrigeratorReading = new RefrigeratorReading(facilityVisitId, {temperature: {value: "."}});

    var status = refrigeratorReading.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });


});