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
function HelpTopicTreeViewController($scope, $timeout, $resource, $location, $route, HelpTopicList,HelpDocumentList) {
    var tree;
    var rawTreeData;
    var myTreeData = getTree(rawTreeData, 'id', 'parentHelpTopic');
    $scope.tree_data = myTreeData;
    $scope.my_tree = tree = {};
    $scope.expanding_property = "name";
    $scope.col_defs = [
        { field: "level"}

    ];

    HelpDocumentList.get({}, function (data) {

        $scope.helpDocumentList = data.helpDocumentList;



    }, function (data) {


        $location.path($scope.$parent.sourceUrl);


    });
//    rawTreeData=$resource('/helpTopicList');
    HelpTopicList.get({}, function (data) {

        rawTreeData = data.helpTopicList;
        myTreeData = getTree(rawTreeData, 'id', 'parentHelpTopic');
        $scope.tree_data = myTreeData;


    }, function (data) {


        $location.path($scope.$parent.sourceUrl);


    });


    $scope.my_tree_handler = function (branch) {
//        <a href="" ng-click="editHelpTopic(ht.id)">{{ht.name}}</a>
        $scope.selectedHelpTopic = branch;
//            $location.path('create/' + branch.id);

    };
    $scope.add_help_topic = function () {
//        <a href="" ng-click="editHelpTopic(ht.id)">{{ht.name}}</a>

        if ($scope.selectedHelpTopic) {
           // alert(' here i am');
            $location.path('create/' + $scope.selectedHelpTopic.id);
        } else {
            $location.path('/create/');
        }

    };
    $scope.add_help_content = function () {
//        <a href="" ng-click="editHelpTopic(ht.id)">{{ht.name}}</a>

        $location.path('createHelpContent/' + $scope.selectedHelpTopic.id);

    };
    $scope.editHelpTopic = function () {

        if ($scope.selectedHelpTopic.category) {
            $location.path('/edit/' + $scope.selectedHelpTopic.id);
        }

        else {
            $location.path('/editHelpContent/' + $scope.selectedHelpTopic.id);
        }
    };
    function getTree(data, primaryIdName, parentIdName) {
        if (!data || data.length === 0 || !primaryIdName || !parentIdName)
            return [];

        var tree = [],
            rootIds = [],
            item = data[0],
            primaryKey = item[primaryIdName],
            treeObjs = {},
            parentId,
            parent,
            len = data.length,
            i = 0;

        while (i < len) {
            item = data[i++];
            primaryKey = item[primaryIdName];
            treeObjs[primaryKey] = item;
            parentId = item[parentIdName];

            if (parentId) {
                parent = treeObjs[parentId];

                if (parent.children) {
                    parent.children.push(item);
                }
                else {
                    parent.children = [item];
                }
            }
            else {
                rootIds.push(primaryKey);
            }
        }

        for (var ii = 0; ii < rootIds.length; ii++) {
            tree.push(treeObjs[rootIds[ii]]);
        }


        return tree;
    }

}
