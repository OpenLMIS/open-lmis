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
function OrderFillRateController($scope, $window, OrderFillRateReport, GetPushedProductList) {
    //to minimize and maximize the filter section
    $scope.wideOption = {'multiple': true, dropdownCss: { 'min-width': '500px' }};
    $scope.OnFilterChanged = function () {
        // clear old data if there was any
        $scope.pusheditems = $scope.data = $scope.datarows = $scope.summaries = [];
        $scope.filter.max = 10000;
        OrderFillRateReport.get($scope.getSanitizedParameter(), function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.summaries = data.pages.rows[0].summary;
                $scope.data = data.pages.rows[0].details;

                $scope.paramsChanged($scope.tableParams);
            }
        });


        GetPushedProductList.get($scope.getSanitizedParameter(),function (data) {
                if (data.pages !== undefined && data.pages.rows !== undefined) {
                    $scope.pusheditems = data.pages.rows;
                }
            });
    };

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.getSanitizedParameter());
        var url;
        if (type == "pushed-product-list") {
            url = '/reports/download/pushed_product_list/' + "pdf" + '?' + params;
        } else {
            url = '/reports/download/order_fill_rate/' + type + '?' + params;
        }
        $window.open(url);
    };
}
