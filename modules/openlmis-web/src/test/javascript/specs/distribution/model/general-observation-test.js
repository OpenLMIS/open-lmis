/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('General Observations', function () {
  it('should return empty if no fields filled', function () {
    var generalObservation = new GeneralObservation({});

    var status = generalObservation.computeStatus();

    expect(status).toEqual('is-empty');
  });

  it('should return incomplete if observations not present', function () {
    var generalObservation = new GeneralObservation({verifiedBy: {name: 'something', title: 'title'}, confirmedBy: {name: 'something', title: 'title'}});

    var status = generalObservation.computeStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should return incomplete if verified By Name not present', function () {
    var generalObservation = new GeneralObservation({observations: "blah blah blah", verifiedBy: {name: '', title: 'title'}, confirmedBy: {name: 'something', title: 'title'}});

    var status = generalObservation.computeStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should return empty if general observations is undefined', function () {
    var generalObservation = new GeneralObservation();

    var status = generalObservation.computeStatus();

    expect(status).toEqual('is-empty');
  });

  it('should return complete if general observations valid', function () {
    var generalObservation = new GeneralObservation({observations: "blah blah blah", verifiedBy: {name: 'Pintu', title: 'title'}, confirmedBy: {name: 'something', title: 'title'}});

    var status = generalObservation.computeStatus();

    expect(status).toEqual('is-complete');
  });

  it('should retain its status', function () {
    var generalObservation = new GeneralObservation({observations: "blah blah blah", verifiedBy: {name: 'Pintu', title: 'title'}, confirmedBy: {name: 'something', title: 'title'}});

    generalObservation.computeStatus();

    expect(generalObservation.status).toEqual('is-complete');
  });
});