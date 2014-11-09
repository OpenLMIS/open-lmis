/**
 * Created by seifu on 10/19/2014.
 */
function HelpDashboardContentController($scope, $location,HelpUsertopicList){
    $scope.title = "Help Content List View";
    ////alert('here');
    // all products list
    HelpUsertopicList.get({}, function (data) {
        $scope.helpTopicList = data.helpTopicList;
    }, function (data) {
        ////alert('successfully loaded');
        $location.path($scope.$parent.sourceUrl);
    });
//    this is to edit the help category
//    $scope.editHelpTopic = function (id) {
//        ////alert(' here to edit');
////        var data = {query: $scope.query};
////        navigateBackService.setData(data);
////        sharedSpace.setCountOfDonations(donationCount);
//        $location.path('/edit/:' + id);
//    };

    $scope.viewHelpContent = function (id) {
        ////alert(' nave to help content view');
//        var data = {query: $scope.query};
//        navigateBackService.setData(data);
//        sharedSpace.setCountOfDonations(donationCount);
        $location.path('/viewhelp/' + id);
    };
}
