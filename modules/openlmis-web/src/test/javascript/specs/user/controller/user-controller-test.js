/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("User", function () {

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(module('ui.bootstrap.dialog'));

  describe("User Controller", function () {

    var scope, $httpBackend, ctrl, user, location, roles, messageService;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location, _messageService_) {
      messageService = _messageService_;
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      location = $location;
      roles = [
        {
          "id": 1,
          "name": "Admin",
          "type": "ADMIN"
        },
        {
          "id": 2,
          "name": "Store In-Charge",
          "type": "REQUISITION"
        },
        {
          "id": 3,
          "name": "Medical Officer",
          "type": "ALLOCATION"
        }
      ];


      spyOn(messageService, 'get').andCallFake(function (value) {
        if (value == 'label.active') return "Active"
        if (value == 'label.inactive') return "Inactive"
      });

      var deliveryZones = [
        {id: 1},
        {id: 2},
        {id: 3},
        {id: 4}
      ];

      var programs = [
        {"id": 1, active: false, push: false},
        {id: 2, active: true, push: false},
        {id: 3, active: true, push: true}
      ];

      var user = {"id": 123, "userName": "User420"};

      ctrl = $controller(UserController, {$scope: scope, roles: roles, programs: programs,
        supervisoryNodes: [], user: user, deliveryZones: deliveryZones}, $location);
      scope.userForm = {$error: { pattern: "" }};
    }));

    it('should group programs by type', function() {
      expect(scope.programsMap.pull).toEqual([{"id": 1, active: false, push: false, status : 'Inactive'}, {id: 2, active: true, push: false, status: 'Active'}]);
      expect(scope.programsMap.push).toEqual([{"id": 3, active: true, push:true, status: 'Active'}]);
    })

    it('should populate role map in scope', function () {

      expect(scope.rolesMap.ADMIN.length).toBe(1);
      expect(scope.rolesMap.REQUISITION.length).toBe(1);
      expect(scope.rolesMap.ALLOCATION.length).toBe(1);
      expect(scope.rolesMap.ADMIN[0].id).toBe(1);
      expect(scope.rolesMap.ADMIN[0].type).toBe("ADMIN");
      expect(scope.rolesMap.REQUISITION[0].id).toBe(2);
      expect(scope.rolesMap.REQUISITION[0].type).toBe("REQUISITION");
      expect(scope.rolesMap.ALLOCATION[0].id).toBe(3);
      expect(scope.rolesMap.ALLOCATION[0].type).toBe("ALLOCATION");
    });

    it('should set programs in scope with added status', function () {
      expect(scope.programsMap).toEqual({pull: [
        {"id": 1, active: false, status: 'Inactive', push: false},
        {id: 2, active: true, status: 'Active', push: false}
      ], push: [
        {"id": 3, active: true, status: 'Active', push: true}
      ]});
    });

    it('should set supervisory nodes in scope', function () {
      expect(scope.supervisoryNodes).toEqual([]);
    });

    it('should update user successful', function () {
      scope.user = {"id": 123, "userName": "User420"};
      $httpBackend.expectPUT('/users/123.json', scope.user).respond(200, {"success": "Saved successfully", "user": {id: 123}});

      scope.saveUser();
      $httpBackend.flush();

      expect(scope.message).toEqual("Saved successfully");
      expect(scope.user).toEqual({id: 123});
      expect(scope.showError).toBeFalsy();
      expect(scope.error).toEqual("");
      expect(location.path()).toBe('/');
    });

    it('should give error message if save not successful and not redirect the user', function () {
      scope.user = {"userName": "User420"};
      $httpBackend.expectPOST('/users.json').respond(400, {"error": "errorMsg"});
      var path = '/create';
      location.path(path);
      scope.saveUser();
      $httpBackend.flush();
      expect("errorMsg").toEqual(scope.error);
      expect(scope.showError).toBeTruthy();
      expect(location.path()).toBe(path);
    });

    it("should throw error when username contains space", function () {
      scope.user = {"userName": "User 420"};
      scope.validateUserName();
      expect(scope.userNameInvalid).toBeTruthy();
    });

    it("should get facilities when user enters 3 characters in search", function () {
      var facilityResponse = {"facilityList": [
        {"code": "F101"}
      ]};
      $httpBackend.expectGET('/facilities.json?searchParam=F10').respond(facilityResponse);

      scope.query = "F10";
      scope.showFacilitySearchResults();

      $httpBackend.flush();
      expect(scope.filteredFacilities).toEqual([
        {"code": "F101"}
      ]);
    });

    it("should filter facilities by facility code when more than 3 characters are entered for search", function () {
      scope.facilityList = [
        {"name": "Village1", "code": "F10111"},
        {"name": "Village2", "code": "F10200"}
      ];

      scope.query = "F101";
      scope.showFacilitySearchResults();

      expect(scope.filteredFacilities).toEqual([
        {"name": "Village1", "code": "F10111"}
      ]);
    });

    it("should filter facilities by facility name when more than 3 characters are entered for search", function () {
      scope.facilityList = [
        {"name": "Village Dispensary", "code": "F10111"},
        {"name": "Facility2", "code": "F10200"}
      ];

      scope.query = "Vill";
      scope.showFacilitySearchResults();

      expect(scope.filteredFacilities).toEqual([
        {"name": "Village Dispensary", "code": "F10111"}
      ]);
    });

    it("should not create a user with empty role", function () {
      var userWithoutRole = {userName: "User 123", homeFacilityRoles: [
        {programId: 111, roleIds: [1]},
        {programId: 222}
      ]};
      scope.userForm = {$error: { required: false}};
      scope.user = userWithoutRole;

      expect(scope.saveUser()).toEqual(false);
    });

    it("should create a user with role assignments, if all required fields are present, and jump to search user page", function () {
      var userWithRoleAssignments = {userName: "User 123", homeFacilityRoles: [
        {programId: 111, roleIds: [1, 2, 3]},
        {programId: 222, roleIds: [1]}
      ]};
      scope.userForm = {$error: { required: false}};
      scope.user = userWithRoleAssignments;
      location.path("create");
      $httpBackend.expectPOST('/users.json', userWithRoleAssignments).respond(200, {"success": "Saved successfully", user: {id: 500}});

      expect(scope.saveUser()).toEqual(true);
      $httpBackend.flush();
      expect(scope.message).toEqual("Saved successfully");
      expect(scope.user).toEqual({id: 500});
      expect(scope.showError).toBeFalsy();
      expect(scope.error).toEqual("");
      expect(location.path()).toBe('/');
    });

    it("should create a user without role assignment, if all required fields are present", function () {
      var userWithoutRoleAssignment = {userName: "User 123"};
      scope.userForm = {$error: { required: false}};
      scope.user = userWithoutRoleAssignment;
      $httpBackend.expectPOST('/users.json', userWithoutRoleAssignment).respond(200, {"success": "Saved successfully", user: {id: 500}});
      location.path('/create');
      expect(scope.saveUser()).toEqual(true);
      $httpBackend.flush();
      expect(scope.message).toEqual("Saved successfully");
      expect(scope.user).toEqual({id: 500});
      expect(scope.showError).toBeFalsy();
      expect(scope.error).toEqual("");
      expect(location.path()).toBe('/');
    });

    it('should set facilitySelected in scope, whenever user selects a facility as "My Facility" when supported programs are not populated', function () {
      var facility = {id: 74, code: 'F10', name: 'facilityName'};
      scope.allSupportedPrograms = undefined;

      var data = {};
      data.facility = facility;
      $httpBackend.expectGET('/facilities/' + facility.id + '.json').respond(data);

      scope.setSelectedFacility(facility);
      $httpBackend.flush();

      expect(scope.facilitySelected).toEqual(facility);
    });

    it('should set facilitySelected in scope, whenever user selects a facility as "My Facility" when supported programs are populated', function () {
      var facility = {id: 74, code: 'F10', name: 'facilityName'};
      scope.allSupportedPrograms = [
        {id: 1, code: 'HIV'},
        {id: 2, code: 'ARV'}
      ];

      var data = {};
      data.facility = facility;

      scope.setSelectedFacility(facility);

      expect(scope.facilitySelected).toEqual(facility);
    });

    it('should clear everything including role assignments when user clears facility', function () {
      scope.clearSelectedFacility();

      expect(scope.facilitySelected).toEqual(null);
      expect(scope.allSupportedPrograms).toEqual(null);
      expect(scope.user.homeFacilityRoles).toEqual(null);
      expect(scope.user.facilityId).toEqual(null);
    });

  });

  describe("User controller resolve", function () {
    var $httpBackend, ctrl, $timeout, $route, $q;
    var deferredObject;
    var user = {"id": 123, "userName": "User420"};

    beforeEach(module('openlmis.services'));

    beforeEach(inject(function (_$httpBackend_, $controller, _$timeout_) {
      $httpBackend = _$httpBackend_;
      deferredObject = {promise: {id: 1}, resolve: function () {
      }};
      spyOn(deferredObject, 'resolve');

      $q = {defer: function () {
      }};

      spyOn($q, 'defer').andCallFake(function () {
        return deferredObject;
      });

      $timeout = _$timeout_;
      ctrl = $controller;
      $route = {current: {params: {userId: 1}}};
    }));

    it('should get user if user id present in route', function () {
      $httpBackend.expect('GET', "/users/1.json").respond({user: user});
      ctrl(UserController.resolve.user, {$q: $q, $route: $route});
      expect($q.defer).toHaveBeenCalled();
      $timeout.flush();
      $httpBackend.flush();
      expect(deferredObject.resolve).toHaveBeenCalledWith(user);
    });

    it('should not make a call for getting user when user id does not exist in route', function () {
      $route.current.params.userId = undefined;
      ctrl(UserController.resolve.user, {$q: $q, $route: $route});
      expect($q.defer).not.toHaveBeenCalled();
    });

    it('should get all roles', function () {
      var roles = [
        {id: 1}
      ];
      $httpBackend.expect('GET', "/roles.json").respond({roles: roles});
      ctrl(UserController.resolve.roles, {$q: $q});
      expect($q.defer).toHaveBeenCalled();
      $timeout.flush();
      $httpBackend.flush();
      expect(deferredObject.resolve).toHaveBeenCalledWith(roles);
    });

    it('should get all programs', function () {
      var programs = [
        {id: 3}
      ];
      $httpBackend.expect('GET', "/programs.json").respond({programs: programs});
      ctrl(UserController.resolve.programs, {$q: $q});
      expect($q.defer).toHaveBeenCalled();
      $timeout.flush();
      $httpBackend.flush();
      expect(deferredObject.resolve).toHaveBeenCalledWith(programs);
    });

    it('should get all supervisory nodes', function () {
      var supervisoryNodes = [
        {id: 5},{id: 7}
      ];
      $httpBackend.expect('GET', "/supervisory-nodes.json").respond({supervisoryNodes: supervisoryNodes});
      ctrl(UserController.resolve.supervisoryNodes, {$q: $q});
      expect($q.defer).toHaveBeenCalled();
      $timeout.flush();
      $httpBackend.flush();
      expect(deferredObject.resolve).toHaveBeenCalledWith(supervisoryNodes);
    });

    it('should get all delivery zones', function () {
      var deliveryZones = [
        {id: 5},{id: 7}
      ];
      $httpBackend.expect('GET', "/deliveryZones.json").respond({deliveryZones: deliveryZones});
      ctrl(UserController.resolve.deliveryZones, {$q: $q});
      expect($q.defer).toHaveBeenCalled();
      $timeout.flush();
      $httpBackend.flush();
      expect(deferredObject.resolve).toHaveBeenCalledWith(deliveryZones);
    });


  })

});