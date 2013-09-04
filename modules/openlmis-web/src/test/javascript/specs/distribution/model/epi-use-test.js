/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

describe('EPI Use', function () {

  it('should set status as empty if expiration date format is invalid and rest of the form is empty', function () {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {value: 'sdfghjk'}}}
    ]});

    var status = epiUse.computeStatus();

    expect(status).toEqual('is-empty');
  });

  it('should set status as incomplete if expiration date format is invalid and at least one other field is filled', function () {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {value: 'sdfghjk'}, stockAtFirstOfMonth: {notRecorded: true}}}
    ]});

    var status = epiUse.computeStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should set status as incomplete if expiration date format is invalid and rest of the form is valid', function () {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {value: 'sdfghjk'}, stockAtFirstOfMonth: {notRecorded: true}, distributed: {value: 100},
        received: {value: 80}, stockAtEndOfMonth: {value: 200}, loss: {value: 50}
      }}
    ]});

    var status = epiUse.computeStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should set status as incomplete if expiration date is not recorded and rest of the form is valid', function () {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {notRecorded: true}, stockAtFirstOfMonth: {notRecorded: true}, distributed: {value: 100},
        received: {value: 80}, stockAtEndOfMonth: {value: 200}, loss: {value: 50}
      }}
    ]});

    var status = epiUse.computeStatus();

    expect(status).toEqual('is-complete');
  });

  it('should set status as complete if the form is valid', function () {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {value: '11/2012'}, stockAtFirstOfMonth: {notRecorded: true}, distributed: {value: 100},
        received: {value: 80}, stockAtEndOfMonth: {value: 200}, loss: {value: 50}
      }}
    ]});

    var status = epiUse.computeStatus();

    expect(status).toEqual('is-complete');
  });

  it('should set not recorded checkbox for epi use', function() {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {value: '11/2012'}, stockAtFirstOfMonth: {notRecorded: true}, distributed: {value: 100},
        received: {value: 80}, stockAtEndOfMonth: {value: 200}, loss: {value: 50}
      }}
    ]});

    epiUse.setNotRecorded();

    expect(epiUse.productGroups[0].reading.expirationDate.notRecorded).toBeTruthy();
    expect(epiUse.productGroups[0].reading.stockAtEndOfMonth.notRecorded).toBeTruthy();
    expect(epiUse.productGroups[0].reading.stockAtFirstOfMonth.notRecorded).toBeTruthy();
    expect(epiUse.productGroups[0].reading.distributed.notRecorded).toBeTruthy();
    expect(epiUse.productGroups[0].reading.loss.notRecorded).toBeTruthy();
    expect(epiUse.productGroups[0].reading.expirationDate.notRecorded).toBeTruthy();
  });
});