/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


app.filter('offset', function() {
    return function(input, start, end) {
        start = parseInt(start, 10);
        return input.slice(start, end);
    };
});

app.directive('treeView', ['$compile', 'TreeViewService', function($compile, TreeViewService, $scope) {

    return {
        restrict: 'A',

        link: function(scope, elem, attrs) {


            scope.itemsPerPage = 10;
            scope.currentPage = 1;
            scope.items = [];
            scope.maxSize = 5;

            var model = attrs.treeView;
            var isRoot = (!attrs.treeRoot ? true : false);
            var nodeLabel = attrs.nodeLabel || 'label';
            var itemInclude = attrs.itemNgInclude || '';
            var itemIncludeHtml = '';

            if (itemInclude && itemInclude.length > 0) {
                itemIncludeHtml = '<div ng-include="\'' + attrs.itemNgInclude + '\'"></div>';
            }


            // template
            var template =
                '<ul  class="tree-view">' +
                '<li ng-repeat="node in ' + model + '">' +
                '<div>' +
                '<div>' +
                '<i ng-click="toggleNode(node);" ng-show="node.facility == 0" ng-class="!node.collapsed ? \'has-child\' : \'has-child-open\'"><span class="nodeText">{{node.' + nodeLabel + '}}</span></i>' +
                '<label ng-show="node.facility == 1" ng-click="selectNode(node)">' +
                '<input type="checkbox" style="vertical-align: top;" ng-checked="node.selected" ng-model="node.selected" ><span class="nodeText">{{node.' + nodeLabel + '}}</span></label>' +
                '</div>' +
                '</div>' +
                '<div class="tree-view" ng-show="node.collapsed" tree-root="false" >' +
                '<ul  class="tree-view">' +
                '<li ng-repeat="district in node.children">' +
                '<div>' +
                '<div>' +
                '<i ng-click="toggleNode(district);" ng-show="district.facility == 0" ng-class="!district.collapsed ? \'has-child\' : \'has-child-open\'"><span class="nodeText">{{district.' + nodeLabel + '}}</span></i>' +
                '<label ng-show="district.facility == 1" ng-click="selectNode(district)">' +
                '<input type="checkbox"  style="vertical-align: top;" ng-checked="district.selected" ng-model="district.selected" >{{district.' + nodeLabel + '}}</label>' +
                '</div></div>' +
                '<div class="tree-view" ng-show="district.collapsed"  tree-root="false" >' +
                '<ul class="tree-view">' +
                '<li  ng-repeat="facility in district.children">' +
                '<div>' +
                '<div>' +
                '<label ng-click="selectNode(facility)">' +
                '<input type="checkbox" style="vertical-align: top;" ng-checked="facility.selected" ng-model="facility.selected" ><span class="nodeText">{{facility.' + nodeLabel + '}}</span></label>' +
                '</div>' +
                '</div>' +
                '</li>' +
                '</ul>'+
            '</div>' +
            '</li>' +
            '</ul>'+
            '</div>' +
            '</li>' +
            '</ul>';

            // root node
            if (isRoot) {

                // toggle when icon clicked
                scope.toggleNode = function(node) {
                    TreeViewService.toggleNode(node);
                };

                // select when name clicked
                scope.selectNode = function(node) {
                    TreeViewService.selectNode(node);
                };
            }

            var compiledHtml = $compile(template)(scope);

            elem.append(compiledHtml);
        },
        controller: 'TreeViewController'
    };
}]); // end directive

function TreeViewController($scope, $log, TreeViewService, $filter, FacilityGeoTree, $q, $timeout, $http) {

    if (!$scope.dataLoaded) {
        $scope.itemsPerPage = 10;
        $scope.currentPage = 1;
        $scope.items = [];
        $scope.maxSize = 5;


        $scope.setPage = function(pageNo) {
            $scope.currentPage = pageNo;

        };

        $scope.data = TreeViewService.data;

        $scope.setItemDataSet = function(selection) {
            if (selection == "region")
                loadTabContent(TreeViewService.data.regionFacilityTree);
            else if (selection == "district")
                loadTabContent(TreeViewService.data.districtFacility);
            else if (selection == "facility")
                loadTabContent(TreeViewService.data.flatFacility);
        };

        $scope.$watch('currentPage', function() {
            var sliceStartPoint = ($scope.currentPage - 1) * $scope.itemsPerPage;
            var sliceEndPoint = sliceStartPoint + $scope.itemsPerPage;

            $scope.currentPageColOneItems = $scope.items.slice(sliceStartPoint, sliceStartPoint + ($scope.itemsPerPage / 2));
            $scope.currentPageColTwoItems = $scope.items.slice(sliceStartPoint + ($scope.itemsPerPage / 2), sliceEndPoint);

        });

        var loadTabContent = function(item) {
            $scope.currentPage = 1;
            $scope.totalItems = item.length;
            $scope.numPages = Math.ceil(item.length / $scope.itemsPerPage) - 1;
            $scope.items = item;

            var sliceStartPoint = ($scope.currentPage - 1) * $scope.itemsPerPage;
            var sliceEndPoint = sliceStartPoint + $scope.itemsPerPage;

            $scope.currentPageColOneItems = item.slice(sliceStartPoint, sliceStartPoint + ($scope.itemsPerPage / 2));
            $scope.currentPageColTwoItems = item.slice(sliceStartPoint + ($scope.itemsPerPage / 2), sliceEndPoint);
        };

        loadTabContent(TreeViewService.data.regionFacilityTree);

        $scope.$watch(function() {
            return TreeViewService.selectedNode;
        }, function() {
            $scope.selectedNode = TreeViewService.selectedNode;
        });

        $scope.dataLoaded = true;
    }
}

app.directive('facilityTypeahead', ['$compile',
    function($compile) {

        return {
            restrict: 'E',
            scope: {
                selectedFacility: '=',
                //bindAttr: '='
            },
            link: function(scope, elm, attr) {
                console.log(attr);
            },
            template: '<input type="text"  ng-model="selectedFacility" typeahead="facility.name for facility in facilities | filter:$viewValue | limitTo:8" class="form-control">' +
                '<input type="button" class="btn btn-primary" style="vertical-align: top" ng-click="open(\'lg\')" value="Lookup">',
            controller: 'ModalCtrl'
        };
    }
]);

app.controller('ModalCtrl', function($scope, $modal, $log, FacilityGeoTree, TreeViewService) {

    $scope.animationsEnabled = true;

    FacilityGeoTree.get({}, function(data) {
        $scope.facilities = data.flatFacility;
        TreeViewService.setData(data);
    });

    $scope.open = function(size) {

        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: '/public/pages/template/facility-selector/facility-selector.html',
            controller: 'ModalInstanceCtrl',
            size: size,
            resolve: {
                items: function() {
                    return $scope.items;
                }
            }
        });

        modalInstance.result.then(function(selectedItem) {
            $scope.selectedFacility = selectedItem.name;

        }, function() {
            //$log.info('Modal dismissed at: ' + new Date());
        });
    };

    $scope.toggleAnimation = function() {
        $scope.animationsEnabled = !$scope.animationsEnabled;
    };

});
app.controller('ModalInstanceCtrl', function($scope, $modalInstance, items, TreeViewService) {

    $scope.items = items;
    $scope.ok = function() {
        $modalInstance.close(TreeViewService.selectedNode);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});
app.factory('TreeViewService', function() {
    var factory = {};

    factory.selectedNode = null;
    factory.data = null;

    factory.setData = function(data) {
        factory.data = data;
    };

    factory.unselectNode = function() {
        if (factory.selectedNode) factory.selectedNode.selected = undefined;

        factory.selectedNode = null;
    };

    factory.selectNode = function(node) {
        if (factory.selectedNode) factory.selectedNode.selected = undefined;

        factory.selectedNode = node;

        node.selected = true;

    };

    factory.toggleNode = function(node) {

        // no node selected
        if (!node) return;

        // no children
        if (!node.children) return;

        // collapse / expand
        if (node.children && node.children.length > 0) {
            node.collapsed = !node.collapsed;
        }
    };

    return factory;
}); // End factory