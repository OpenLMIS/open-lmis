/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
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