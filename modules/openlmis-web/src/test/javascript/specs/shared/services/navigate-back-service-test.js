/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('navigateBackService', function() {
  var navigateBackService;

  beforeEach(module('openlmis.services'));

  beforeEach(inject(function(_navigateBackService_) {
    navigateBackService = _navigateBackService_;
  }));

  it('should save the json data passed to it', function() {
    var data = {name: "Riya", marks: 100};
    navigateBackService.setData(data);
    expect({name: navigateBackService.name,marks: navigateBackService.marks}).toEqual(data);
  });
});