/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('DistributionListController', function () {

  var scope, location;

  var mockedIndexedDB = {
    getConnection: function () {
      return {
        transaction: function () {
          return {
            objectStore: function () {
              return {
                openCursor: function () {
                  return {}
                }
              }
            }
          };
        }
      }
    }
  };

  beforeEach(inject(function ($rootScope, $location, $controller) {
    scope = $rootScope.$new();
    location = $location;
    var controller = $controller;
    controller(DistributionListController, {$scope: scope, $location: location, IndexedDB: mockedIndexedDB })
  }));

  it('should set location path', function () {
    var zpp = "1_2_3";
    var path = "/record-facility-data/" + zpp;
    var locationPath = spyOn(location, 'path').andCallThrough();

    scope.recordFacilityData(zpp);

    expect(locationPath).toHaveBeenCalledWith(path);
  });
});