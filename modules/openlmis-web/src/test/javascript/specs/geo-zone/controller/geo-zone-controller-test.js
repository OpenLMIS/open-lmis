/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Geographic Zone Controller", function () {

  var scope, httpBackend, ctrl, location, geoLevels, geoZone, response;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location) {
    scope = $rootScope.$new();
    httpBackend = _$httpBackend_;
    location = $location;

    geoLevels = [
      {"id": 1, "code": "country", "name": "Country", "levelNumber": 1},
      {"id": 2, "code": "state", "name": "State", "levelNumber": 2},
      {"id": 3, "code": "province", "name": "Province", "levelNumber": 3},
      {"id": 4, "code": "district", "name": "District", "levelNumber": 4}
    ];
    geoZone = {"id": 1, "code": "state", "name": "state", "level": {"code": "state", "name": "state"}};

    response = {"geographicZoneList": [
      {"id": 2, "code": "Mozambique", "name": "Mozambique", "level": {"name": "Country", "levelNumber": 1}},
      {"id": 1, "code": "Root", "name": "Root", "level": {"name": "Country", "levelNumber": 1}}
    ]};

    ctrl = $controller;
    ctrl('GeoZoneController', {$scope: scope, geoLevels: geoLevels, geoZone: geoZone});

    httpBackend.when("GET", '/parentGeographicZones/state.json').respond(response);
    httpBackend.flush();

  }));

  it('should set geoZone and geoLevels in scope', function () {
    expect(scope.levels).toEqual(geoLevels);
    expect(scope.geoZone).toEqual(geoZone);
  });

  it('should set geoZone parent as null if geo zone is at highest level', function () {
    scope.parentLevels = [];
    scope.geoZoneForm = {"$error": {"pattern": false, "required": false}};
    scope.save();
    expect(scope.geoZone.parent).toEqual(undefined);
  });

  it('should load parent if geo zone is present', function () {
    expect(scope.editMode).toBeTruthy();
    expect(scope.parentGeoZones).toEqual(response.geographicZoneList);
    expect(scope.parentLevels).toEqual(['Country']);
  });

  it('should take to search page on cancel', function () {
    scope.cancel();
    expect(scope.$parent.geoZoneId).toBeUndefined();
    expect(scope.$parent.message).toEqual("");
    expect(location.path()).toEqual('/#/search');
  });

  it('should save geo zone', function () {
    ctrl('GeoZoneController', {$scope: scope, geoLevels: geoLevels, geoZone: undefined});
    var newGeoZone = {"code": "state", "name": "state", "level": {"code": "state", "name": "state"}};
    scope.geoZone = newGeoZone;
    scope.geoZoneForm = {"$error": {"pattern": false, "required": false}};

    httpBackend.expectPOST('/geographicZones.json', newGeoZone).respond(200, {"success": "Saved successfully", "geoZone": geoZone});
    scope.save();
    httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.showError).toBeFalsy();
    expect(scope.$parent.message).toEqual("Saved successfully");
    expect(scope.$parent.geoZoneId).toEqual(geoZone.id);
  });

  it('should update geo zone', function () {
    scope.geoZoneForm = {"$error": {"pattern": false, "required": false}};

    httpBackend.expectPUT('/geographicZones/' + geoZone.id + '.json', geoZone).respond(200, {"success": "Saved successfully", "geoZone": geoZone});
    scope.save();
    httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.showError).toBeFalsy();
    expect(scope.$parent.message).toEqual("Saved successfully");
    expect(scope.$parent.geoZoneId).toEqual(geoZone.id);
  });

  it('should throw error if geo zone invalid', function () {
    scope.geoZoneForm = {"$error": {"pattern": false, "required": false}};

    httpBackend.expectPUT('/geographicZones/' + geoZone.id + '.json', geoZone).respond(400, {"error": "failed to update"});
    scope.save();
    httpBackend.flush();

    expect(scope.error).toEqual("failed to update");
    expect(scope.showError).toBeTruthy();
    expect(scope.$parent.message).toEqual("");
  });

  it('should not save geo zone if invalid', function () {
    scope.geoZoneForm = {"$error": {"pattern": true, "required": false}};

    scope.save();

    expect(scope.error).toEqual("form.error");
    expect(scope.showError).toBeTruthy();
    expect(scope.message).toEqual("");
  });

  it('should not save geo zone if invalid', function () {
    scope.geoZoneForm = {"$error": {"pattern": false, "required": true}};

    scope.save();

    expect(scope.error).toEqual("form.error");
    expect(scope.showError).toBeTruthy();
    expect(scope.message).toEqual("");
  });

});