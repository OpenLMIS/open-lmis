/**
 * Created by teklu on 10/19/2014.
 */
var helpModule=angular.module('help', ['openlmis','ui.bootstrap.modal','ui.bootstrap.dialog', 'ui.bootstrap.dropdownToggle','textAngular']);
helpModule.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/list', {controller: HelpCategoryController, templateUrl: 'partials/list.html'}).
        when('/treeView', {controller: HelpTopicTreeViewController, templateUrl: 'partials/topicTreeListView.html'}).
        when('/edit/:id', {controller: HelpCategoryEditController, templateUrl: 'partials/edit.html'}).
        when('/create/:id', {controller: HelpTopicCreateController, templateUrl: 'partials/create.html'}).
        when('/create/', {controller: HelpTopicCreateController, templateUrl: 'partials/create.html'}).
        when('/createHelpContent/:id', {controller: ContentCreateController, templateUrl: 'partials/createHelpContent.html'}).
        when('/editHelpContent/:id', {controller: HelpContentEditCotntroller, templateUrl: 'partials/editHelpContent.html'}).
        otherwise({redirectTo: '/treeView'});
}]).run(function ($rootScope, AuthorizationService) {
    $rootScope.helpTopicSelected = "selected";
//    AuthorizationService.preAuthorize('MANAGE_PRODUCT');
});