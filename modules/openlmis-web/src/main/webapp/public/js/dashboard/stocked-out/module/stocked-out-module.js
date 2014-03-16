/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 3/16/14
 * Time: 9:08 PM
 * To change this template use File | Settings | File Templates.
 */
angular.module('stocked-out',['openlmis', 'ngTable']).config(['$routeProvider', function ($routeProvider) {

        $routeProvider.
            when('/stock', {controller: StockedOutController, templateUrl: 'partials/stocked-out.html', resolve : ResolveDashboardFormData }).
            when('/view-stock-detail/:geographicZoneId/:programId/:periodId/:productId', {controller: StockedOutController, templateUrl: 'partials/stocked-out.html', resolve : ResolveDashboardFormData}).
            otherwise({redirectTo: '/stock'});
    }]).run(function($rootScope){

        $rootScope.stockedOutSelected = 'selected';
        $rootScope.showProductsFilter = true;
    });