/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/26/14
 * Time: 5:22 PM
 */

angular.module('alerts',['openlmis', 'ngTable']).config(['$routeProvider', function ($routeProvider) {

        $routeProvider.
            when('/alerts', {controller: AlertsController, templateUrl: 'partials/alerts.html'}).
            otherwise({redirectTo: '/alerts'});
    }]);
