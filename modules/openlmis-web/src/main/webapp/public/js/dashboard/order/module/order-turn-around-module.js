/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/28/14
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */

angular.module('order',['openlmis', 'ngTable']).config(['$routeProvider', function ($routeProvider) {

        $routeProvider.
            when('/leadTime', {controller: ShipmentLeadTimeController, templateUrl: 'partials/shipment-lead-time.html', resolve : ResolveDashboardFormData }).
            otherwise({redirectTo: '/leadTime'});
    }]).run(function($rootScope){
        $rootScope.orderTurnAroundSelected = 'selected';
        $rootScope.showProductsFilter = false;
    });
