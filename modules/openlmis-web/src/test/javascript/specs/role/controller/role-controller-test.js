/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Role", function () {

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));

  describe("Create Role", function () {
    var ctrl, scope, $httpBackend, rights, dialog, messageService;
    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _$dialog_, _messageService_) {
      scope = $rootScope.$new();
      dialog = _$dialog_;
      $httpBackend = _$httpBackend_;
      messageService = _messageService_;
      rights = [
        {"name": "CONFIGURE_RNR", "displayNameKey": "configure rnr", "type": "ADMIN"},
        {"name": "MANAGE_FACILITY", "displayNameKey": "manage facility", "type": "ADMIN"},
        {"name": "CREATE_REQUISITION", "displayNameKey": "create requisition", "type": "REQUISITION"},
        {"name": "VIEW_REQUISITION", "displayNameKey": "view requisition", "type": "REQUISITION"},
        {"name": "FILL_SHIPMENT", "displayNameKey": "fill shipment", "type": "FULFILLMENT"},
        {"name": "REPORTING", "displayNameKey": "reporting right", "type": "REPORTING"}
      ];
      $httpBackend.when('GET', '/rights.json').respond(200, {"rights": rights});
      ctrl = $controller(RoleController, {$scope: scope, $dialog: dialog});
    }));

    it('should get all rights and separate them into admin and non-admin rights', function () {
      $httpBackend.flush();
      expect(scope.rights).toEqual(rights);
      expect(scope.adminRights).toEqual([
        {"name": "CONFIGURE_RNR", "displayNameKey": "configure rnr", "type": "ADMIN"},
        {"name": "MANAGE_FACILITY", "displayNameKey": "manage facility", "type": "ADMIN"}
      ]);
      expect(scope.requisitionRights).toEqual([
        {"name": "CREATE_REQUISITION", "displayNameKey": "create requisition", "type": "REQUISITION"},
        {"name": "VIEW_REQUISITION", "displayNameKey": "view requisition", "type": "REQUISITION"}
      ]);
      expect(scope.fulfillmentRights).toEqual([
        {"name": "FILL_SHIPMENT", "displayNameKey": "fill shipment", "type": "FULFILLMENT"}
      ]);
      expect(scope.reportingRights).toEqual([
        {"name": "REPORTING", "displayNameKey": "reporting right", "type": "REPORTING"}
      ]);

    });

    it('should create a role', function () {
      $httpBackend.expectPOST('/roles.json').respond({"success": "Saved successfully"});
      scope.role.name = "roleName";
      scope.role.rights = ["right1"];

      scope.saveRole();
      $httpBackend.flush();
    });

    it('should deSelect all rights when admin/program-based role type is toggled', function () {
      scope.role.rights.push(rights[2]);
      scope.role.adminRole = "false";
      window.selected = true;
      scope.dialogCloseCallback(true);

      expect(scope.role.rights.length).toEqual(0);
      expect(scope.roleTypeModal).toBeFalsy();
      expect(scope.role.adminRole).toBeTruthy();
      expect(scope.showRightError).toBeFalsy();
      expect(scope.showError).toBeFalsy();
    });

    it('should retain selection if cancel is clicked', function () {
      scope.role.rights.push(rights[2]);
      scope.role.adminRole = "true";
      window.selected = true;
      scope.dialogCloseCallback(false);

      expect(scope.role.rights.length).toEqual(1);
      expect(scope.roleTypeModal).toBeFalsy();
      expect(scope.role.adminRole).toBeTruthy();
    });

    it("should display role type modal when radio button is clicked", function () {
      scope.roleTypeModal = false;
      $httpBackend.expectGET('/public/pages/template/dialog/dialogbox.html').respond(200);
      scope.showRoleTypeModal('true');
      expect(window.selected).toBeTruthy();
    });
  });

  describe("Edit Role", function () {

    var ctrl, scope, httpBackend, location, role, rightList;

    it('should update a role', function () {
      inject(function ($rootScope, _$httpBackend_, $controller, $location, _$dialog_) {
        role = {"id": 123, "name": "Admin", "adminRole": 'true', "rights": [
          {"name": "CONFIGURE_RNR", "displayNameKey": "configure rnr"}
        ]};
        rightList = [
          {"name": "CONFIGURE_RNR", "displayNameKey": "configure rnr"}
        ];
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        location = $location;
        httpBackend.when('GET', '/rights.json').respond({"rights": rightList});
        httpBackend.when('GET', '/roles/123.json').respond({"role": role, "right_type": "ADMIN"});
        ctrl = $controller(RoleController, {$scope: scope, $routeParams: {id: 123}, $location: location, $dialog: _$dialog_ });
      });
      httpBackend.expectPUT('/roles/123.json').respond(200,
        {"success": "success"});
      scope.role.name = "name";
      scope.role.rights = rightList;

      scope.saveRole();

      httpBackend.flush();
      expect(scope.message).toEqual("success");
      expect(location.path()).toEqual("/list");

    });
  });

  describe("updateRights()", function () {
    var rightList, role, ctrl, scope, $httpBackend;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
      role = {"id": 1, "name": "Admin", "adminRole": 'true', "rights": [
        {"name": "CONFIGURE_RNR", "displayNameKey": "configure rnr"},
        {"name": "MANAGE_FACILITY", "displayNameKey": "manage facility"}
      ]};
      rightList = [
        {"name": "CONFIGURE_RNR", "displayNameKey": "configure rnr"},
        {"name": "MANAGE_FACILITY", "displayNameKey": "manage facility"},
        {"name": "CREATE_REQUISITION", "displayNameKey": "create requisition"},
        {"name": "VIEW_REQUISITION", "displayNameKey": "view requisition"}
      ];
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      $httpBackend.expectGET('/rights.json').respond({"rights": rightList});
      $httpBackend.expectGET('/roles/123.json').respond({"role": role, "right_type": "ADMIN"});
      ctrl = $controller(RoleController, {$scope: scope, $routeParams: {id: 123} });
      $httpBackend.flush();
      expect(scope.role).toEqual(role);
      expect(scope.role.id).toEqual(role.id);
      expect(scope.currentRightType).toEqual('ADMIN');
    }));

    it('should set the scope', function () {
      expect(scope.rights.length).toEqual(4);
      expect(scope.rights[0].name).toEqual(rightList[0].name);
      expect(scope.rights[0].selected).toBeTruthy();
      expect(scope.rights[2].name).toEqual(rightList[2].name);
      expect(scope.rights[2].selected).toBeUndefined();
    });

    it('should add the selected rights to the role', function () {
      scope.updateRights(rightList[3]);

      expect(scope.role.rights.length).toEqual(3);
      expect(scope.showRightError).toBeFalsy();
      expect(scope.role.rights[0]).toEqual(rightList[0]);
      expect(scope.role.rights[1]).toEqual(rightList[1]);
      expect(scope.role.rights[2]).toEqual(rightList[3]);
    });

    it('should remove the right from role, if not selected', function () {
      var right = {"name": "MANAGE_FACILITY", "displayNameKey": "manage facility", "selected": true};
      scope.updateRights(right);
      expect(scope.role.rights.length).toEqual(1);
      expect(scope.role.rights[0]).toEqual(rightList[0]);
    });

    it('should add dependent right to role', function () {
      scope.updateRights(rightList[2]);
      expect(scope.role.rights.length).toEqual(4);
      expect(scope.role.rights[2].name).toEqual(rightList[2].name);
      expect(scope.role.rights[3].name).toEqual(rightList[3].name);
    });

    it('should not remove dependent right to role on deselection', function () {

      scope.updateRights(rightList[2]);
      expect(scope.role.rights.length).toEqual(4);
      scope.updateRights(rightList[2]);
      expect(scope.role.rights.length).toEqual(3);
      expect(scope.role.rights[2].name).toEqual(rightList[3].name);
    });

    it("should return true when rights that this right depends upon is selected", function () {
      scope.role.rights.push(rightList[2]);
      expect(scope.areRelatedFieldsSelected(rightList[3])).toBeTruthy();
    });

    it("should return false when rights that this right depends upon is not selected", function () {
      expect(scope.areRelatedFieldsSelected(rightList[3])).toBeFalsy();
    });

    it('should not add dependent right (VIEW_REQUISITION) to the role rights list if it is already present', function () {
      scope.role.rights.push(rightList[3]);
      expect(scope.role.rights.length).toEqual(3);

      scope.updateRights(rightList[2]);

      expect(scope.role.rights.length).toEqual(4);
      expect(scope.role.rights[0]).toEqual(rightList[0]);
      expect(scope.role.rights[1]).toEqual(rightList[1]);
      expect(scope.role.rights[2]).toEqual(rightList[3]);
      expect(scope.role.rights[3]).toEqual(rightList[2]);
    });
  });
});

