angular.module('requisition-report', ['openlmis', 'angularCombine', 'ngTable', 'ui.bootstrap.dialog', 'angularjs-dropdown-multiselect']).config(['$routeProvider',
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
                when('/app-version', {
                    controller: VersionReportController,
                    templateUrl: 'appVersion/partials/list.html',
                    reloadOnSearch: false
                }).
                when('/stock-out', {
                    controller: ProductReportController("stockOut"),
                    templateUrl: 'stockOut/partials/list.html',
                    reloadOnSearch: false
                }).otherwise({redirectTo: '/'});
        }]).run(
    function ($rootScope, AuthorizationService) {

    }
).config(function (angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    }).filter("districtFilter", DistrictFilter)
    .filter("provinceFilter", ProvinceFilter)
    .filter("facilityFilter", FacilityFilter);