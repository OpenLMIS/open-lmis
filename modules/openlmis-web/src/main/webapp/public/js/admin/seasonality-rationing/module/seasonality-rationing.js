/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

var seasonalityRationing = angular.module('seasonalityRationing', ['openlmis', 'ngTable', 'ui.bootstrap.modal', 'ui.bootstrap.dialog', 'ui.bootstrap.dropdownToggle']);
seasonalityRationing.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/list', {controller: SeasonRationingAdjustmentTypeController, templateUrl: 'partials/seasonality-rationing-adjustment-type.html', resolve: SeasonRationingAdjustmentTypeController.resolve}).
        when('/create', {controller: SeasonRationingAdjustmentTypeCreateController, templateUrl: 'partials/seasonality-rationing-adjustment-type-create.html'}).
        when('/edit/:id', {controller: SeasonRationingAdjustmentTypeEditCotntroller, templateUrl: 'partials/seasonality-rationing-adjustment-type-update.html', resolve: SeasonRationingAdjustmentTypeEditCotntroller.resolve}).
        when('/list_adustment_factor', {controller: AdjustmentBasisFormulaController, templateUrl: 'partials/adjustment-basis-formula.html', resolve: AdjustmentBasisFormulaController.resolve}).
        when('/create_adustment_factor', {controller: AdjustmentBasisCreateController, templateUrl: 'partials/adjustment-basis-formula-create.html'}).
        when('/edit_adustment_factor/:id', {controller: AdjustementBasisEditCotntroller, templateUrl: 'partials/adjustment-basis-formula-update.html'}).
        otherwise({redirectTo: '/list'});
}]).run(function ($rootScope, AuthorizationService) {
    $rootScope.helpTopicSelected = "selected";
//    AuthorizationService.preAuthorize('MANAGE_PRODUCT');
});