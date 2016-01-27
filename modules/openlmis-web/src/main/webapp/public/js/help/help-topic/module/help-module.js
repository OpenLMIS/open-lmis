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
var helpModule=angular.module('help', ['openlmis','ui.bootstrap.modal','ui.bootstrap.dialog', 'ui.bootstrap.dropdownToggle','textAngular']);
helpModule.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/list', {controller: HelpCategoryController, templateUrl: 'partials/list.html'}).
        when('/treeView', {controller: HelpTopicTreeViewController, templateUrl: 'partials/topicTreeListView.html'}).
        when('/edit/:id', {controller: HelpCategoryEditController, templateUrl: 'partials/edit.html'}).
        when('/create/:id', {controller: HelpTopicCreateController, templateUrl: 'partials/create.html'}).
        when('/create/', {controller: HelpTopicCreateController, templateUrl: 'partials/create.html'}).
        when('/uploadDocument/', {controller: HelpUploadController, templateUrl: 'partials/uploadDocument.html'}).
        when('/createHelpContent/:id', {controller: ContentCreateController, templateUrl: 'partials/createHelpContent.html'}).
        when('/editHelpContent/:id', {controller: HelpContentEditCotntroller, templateUrl: 'partials/editHelpContent.html'}).
        otherwise({redirectTo: '/treeView'});
}]).run(function ($rootScope, AuthorizationService) {
    $rootScope.helpTopicSelected = "selected";
//    AuthorizationService.preAuthorize('MANAGE_PRODUCT');
});
