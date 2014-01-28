/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Navigation", function () {

    beforeEach(module('openlmis'));

    describe("NavigationController", function () {

        var scope, ctrl;

        beforeEach(inject(function ($rootScope, $controller,_localStorageService_) {
            scope = $rootScope.$new();
            localStorageService = _localStorageService_;
            spyOn(localStorageService, 'get').andReturn('MANAGE_FACILITY,UPLOADS');
            //localStorageService.add(localStorageKeys.RIGHT,'MANAGE_FACILITY,UPLOADS'); [Code runs in browser but is not supported on jasmine headless mode ]
            ctrl = $controller(NavigationController, {$scope:scope , localStorageService:localStorageService});
        }));

        it('should check permission', function () {
            expect(true).toEqual(scope.hasPermission("MANAGE_FACILITY"));
            expect(false).toEqual(scope.hasPermission("CREATE_REQUISITION"));
        });


        it('should set user rights into scope', function () {
            expect(scope.rights).toEqual('MANAGE_FACILITY,UPLOADS');
        });
    })
});