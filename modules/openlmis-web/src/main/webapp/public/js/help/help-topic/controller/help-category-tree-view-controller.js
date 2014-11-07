function HelpTopicTreeViewController($scope, $timeout, $resource, $location, $route, HelpTopicList) {
    var tree;
    var rawTreeData;
    var myTreeData = getTree(rawTreeData, 'id', 'parentHelpTopic');
    $scope.tree_data = myTreeData;
    $scope.my_tree = tree = {};
    $scope.expanding_property = "name";
    $scope.col_defs = [
        { field: "level"}

    ];


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

//        var data = {query: $scope.query};
//        navigateBackService.setData(data);
//        sharedSpace.setCountOfDonations(donationCount);
        ////alert(" editing content"+$scope.selectedHelpTopic.category);
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



