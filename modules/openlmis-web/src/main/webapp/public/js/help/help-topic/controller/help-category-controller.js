/**
 * Created by seifu on 10/19/2014.
 */
function HelpCategoryController($scope, $location,HelpTopicList ){
    $scope.title = "Manage Help Topic";

    // all products list
    HelpTopicList.get({}, function (data) {
         $scope.helpTopicList = data.helpTopicList;
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });
//    this is to edit the help category
    $scope.editHelpTopic = function (id) {
        //alert(' here to edit');
//        var data = {query: $scope.query};
//        navigateBackService.setData(data);
//        sharedSpace.setCountOfDonations(donationCount);
        $location.path('/edit/' + id);
    };


}
