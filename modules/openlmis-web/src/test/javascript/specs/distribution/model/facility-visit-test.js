/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('Facility Visit', function () {
  it('should return empty if no fields filled', function () {
    var facilityVisit = new FacilityVisit({});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });

  it('should return incomplete if visit details not present', function () {
    var facilityVisit = new FacilityVisit({visited: { value: 'true' } });

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should return incomplete if verified By Name not present', function () {
    var facilityVisit = new FacilityVisit({visited: {value: 'true'}, observations: {value:"blah blah blah"}, verifiedBy: {name: {value: ''}, title: {value: 'title'}}, confirmedBy: {name: {value: 'something'}, title: {value: 'title'}}});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should return empty if visit details is undefined', function () {
    var facilityVisit = new FacilityVisit();

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.EMPTY);
  });

  it('should return complete if visit details valid', function () {
    var facilityVisit = new FacilityVisit({visited: {value: 'true'}, visitDate: {value: '2016-06-10'}, observations: {value:"blah blah blah"}, verifiedBy: {name: {value: 'Pint'}, title: {value: 'title'}}, confirmedBy: {name: {value: 'something'}, title: {value: 'title'}}});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should return complete if visit details valid and observations not filled', function () {
    var facilityVisit = new FacilityVisit({visited: {value: 'true'}, visitDate: {value: '2016-06-10'},
      verifiedBy: {name: {value: 'Pint'}, title: {value: 'title'}}, confirmedBy: {name: {value: 'something'}, title: {value: 'title'}}});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should retain its status', function () {
    var facilityVisit = new FacilityVisit({visited: {value: 'true'}, visitDate: {value: '2016-06-10'}, observations: {value:"blah blah blah"}, verifiedBy: {name: {value: 'Pint'}, title: {value: 'title'}}, confirmedBy: {name: {value: 'something'}, title: {value: 'title'}}});

    facilityVisit.computeStatus();

    expect(facilityVisit.status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should return is-empty if facility visited not filled', function () {
    var facilityVisit = new FacilityVisit({});

    expect(facilityVisit.computeStatus()).toEqual(DistributionStatus.EMPTY);
  });

  it('should return is-incomplete if facility visited partially filled', function () {
    var facilityVisit = new FacilityVisit({visited: {value: 'true'}});

    expect(facilityVisit.computeStatus()).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should return is-complete if facility visited fully filled', function () {
    var facilityVisit = new FacilityVisit({visited: {value: 'true'}, visitDate: {value: '2016-06-10'}, observations: {value:"blah blah blah"}, verifiedBy: {name: {value: 'Pint'}, title: {value: 'title'}}, confirmedBy: {name: {value: 'something'}, title: {value: 'title'}}});

    expect(facilityVisit.computeStatus()).toEqual(DistributionStatus.COMPLETE);
  });

  it('should return is-incomplete if visit info filled but observation data not filled', function () {
    var facilityVisit = new FacilityVisit({visited: {value: 'true'}, visitDate: {value: '2016-06-10'}});

    expect(facilityVisit.computeStatus()).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should return incomplete if not visited but no reason provided fields filled', function () {
    var facilityVisit = new FacilityVisit({visited: {value: 'false'}});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

  it('should return complete if not visited and a reason provided', function () {
    var facilityVisit = new FacilityVisit({visited: {value: 'false'}, reasonForNotVisiting: {value:"BAD WEATHER"}});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should return complete if not visited and a reason selected as other and described', function () {
    var facilityVisit = new FacilityVisit({visited: {value: 'false'}, reasonForNotVisiting: {value:"OTHER"}, otherReasonDescription: {value:"I was ill"}});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.COMPLETE);
  });

  it('should return incomplete if not visited and a reason selected as other but not described', function () {
    var facilityVisit = new FacilityVisit({visited: {value: 'false'}, reasonForNotVisiting: {value:"OTHER"}});

    var status = facilityVisit.computeStatus();

    expect(status).toEqual(DistributionStatus.INCOMPLETE);
  });

});
