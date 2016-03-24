angular.module('requisition-report', ['openlmis', 'angularCombine', 'ngTable', 'ui.bootstrap.dialog']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/requisition', {
                    controller: RequisitionReportController,
                    templateUrl: 'requisition/partials/list.html',
                    reloadOnSearch: false
                }).
                when('/stock-on-hand-single-product', {
                    controller: SingleProductReportController,
                    templateUrl: 'stock-on-hand/single-product/partials/list.html',
                    reloadOnSearch: false
                }).
                when('/stock-on-hand-all-products', {
                    controller: SingleFacilityReportController,
                    templateUrl: 'stock-on-hand/all-products/partials/list.html',
                    reloadOnSearch: false
                }).
                when('/app-version', {
                    controller: VersionReportController,
                    templateUrl: 'appVersion/partials/list.html',
                    reloadOnSearch: false
                }).
                when('/stock-out-all-products', {
                    controller: StockOutAllProductsReportController,
                    templateUrl: 'stockout/all-products/partials/list.html',
                    reloadOnSearch: false
                }).
                when('/stock-out-single-product', {
                    controller: StockOutSingleProductReportController,
                    templateUrl: 'stockout/single-product/partials/list.html',
                    reloadOnSearch: false
            }).otherwise({redirectTo: '/'});
        }]).run(
    function ($rootScope, AuthorizationService) {

    }
).config(function (angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    }).filter("districtFilter", DistrictFilter)
    .filter("provinceFilter", ProvinceFilter)
    .filter("facilityFilter", FacilityFilter)
    .directive('datePickerContainer', datePickerContainer);