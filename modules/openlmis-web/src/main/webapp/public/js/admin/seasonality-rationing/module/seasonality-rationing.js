/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
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