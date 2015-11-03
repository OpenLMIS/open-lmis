angular.module('requisition-report', ['openlmis', 'angularCombine', 'ngTable', 'ui.bootstrap.dialog']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/requisition', {
                    controller: RequisitionReportController,
                    templateUrl: 'requisition/partials/list.html',
                    reloadOnSearch: false
                }).
                when('/single-product', {
                    controller: ProductReportController("singleProduct"),
                    templateUrl: 'single-product/partials/list.html',
                    reloadOnSearch: false
                }).
                when('/all-products', {
                    controller: ProductReportController("singleFacility"),
                    templateUrl: 'all-products/partials/list.html',
                    reloadOnSearch: false
                }).
                otherwise({redirectTo: '/'});
        }]).run(
    function ($rootScope, AuthorizationService) {

    }
).config(function (angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    }).filter("districtFilter", DistrictFilter)
    .filter("provinceFilter", ProvinceFilter)
    .filter("facilityFilter", FacilityFilter);