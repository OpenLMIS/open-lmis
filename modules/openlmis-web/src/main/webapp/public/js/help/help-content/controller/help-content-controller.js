/**
 * Created by seifu on 10/19/2014.
 */
function HelpContentController($scope, $location, HelpContentList) {
    $scope.title = "Manage Help Content";
    //////alert('here');
    // all products list
    HelpContentList.get({}, function (data) {
        $scope.helpContentList = data.helpContentList;
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });
//    this is to edit the help category
    $scope.editHelpContent = function (id) {
        $location.path('/edit/' + id);
    };

    $scope.viewHelpContent = function (id) {
        //////alert(' nave to help content view');
//        var data = {query: $scope.query};
//        navigateBackService.setData(data);
//        sharedSpace.setCountOfDonations(donationCount);
        $location.path('/viewhelp/' + id);
    };
}
