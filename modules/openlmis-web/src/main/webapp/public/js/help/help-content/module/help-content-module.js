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
angular.module('help', ['openlmis','ui.bootstrap.modal', 'ui.bootstrap.dialog', 'ui.bootstrap.dropdownToggle','textAngular']).config(['$routeProvider', function ($routeProvider) {

    $routeProvider.
        when('/list',{controller: HelpContentController, templateUrl: 'partials/list.html'}).
        when('/edit/:id', {controller: HelpContentEditCotntroller, templateUrl: 'partials/edit.html'}).
        when('/create', {controller: HelpContentCreateController, templateUrl: 'partials/create.html'}).
        when('/viewhelp/:id', {controller: HelpFileContentController, templateUrl: 'partials/helpDetail.html'}).
        when('/dashboard',{controller: HelpDashboardContentController, templateUrl: 'partials/helpDashboard.html'}).
        when('/viewDashboard',{controller: HelpTreeViewController, templateUrl: 'partials/helpView.html'}).
        otherwise({redirectTo: '/viewDashboard'});
}]).run(function ($rootScope, AuthorizationService) {
    $rootScope.helpContentSelected = "selected";
//    AuthorizationService.preAuthorize('MANAGE_PRODUCT');
});
