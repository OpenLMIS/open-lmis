/*
 *This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.

 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.

 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
describe("Seasoning Rationing Adjustment Type", function () {
    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(module('ngTable'));
//    beforeEach(module('ui.bootstrap.dialog'));
    describe("Add Rationing Adjustement Type", function () {
        var ctrl, scope, $httpBackend, seasonalityRationingsList, dialog, messageService,route,location,filter,  navigateBackService,ngTableParams;
        alert('here');
        beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _$dialog_, _messageService_,_ngTableParams_) {
            alert('here');
            scope = $rootScope.$new();
            dialog = _$dialog_;
            $httpBackend = _$httpBackend_;
            messageService = _messageService_;
//            route = _$route_;
//            location = _$location_;
//            filter = _$filter_;
//            navigateBackService = _navigateBackService_;
            ngTableParams = _ngTableParams_;
            alert('here');
            seasonalityRationingsList = [
                {"name": "Rationing1", "description": " Rationing ", "displayOrder": "1"},
                {"name": "Seasonality", "description": " Seasonalit ", "displayOrder": "2"},
                {"name": "Outbreak", "description": " Outbreak ", "displayOrder": "3"},
                {"name": "Malaria season", "description": " Malaria season ", "displayOrder": "4"},
                {"name": "Remote Facility", "description": " Remote Facility", "displayOrder": "5"},
                {"name": "MSL Physical Inventory close-out", "description": " MSL Physical Inventory close-out ", "displayOrder": "6"},
                {"name": "Other", "description": " Other ", "displayOrder": "7"}

            ];
            $httpBackend.when('GET', '/season-rationing/seasonalityRationingTypes.json?param=').respond(200, {"seasonalityRationings": seasonalityRationingsList});
            ctrl = $controller(SeasonRationingAdjustmentTypeController, {$scope: scope, $dialog: dialog,seasonalityRationingsList:seasonalityRationingsList});
        }));
        it("Should Get All Seasoning Rationing Adjustment Types ", function () {
//            $httpBackend.flush();
            alert(scope.seasonalityRationings);
            expect(scope.seasonalityRationings).toEqual(seasonalityRationingsList);
        });
    });


});

