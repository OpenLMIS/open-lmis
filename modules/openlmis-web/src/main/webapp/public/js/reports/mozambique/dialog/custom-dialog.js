
var MozambiqueDialog = {
    newDialog: function (overrideOpts, callback, $dialog) {
        var defaults = {
            id: "",
            body: "Body",
            ok: {label: "button.ok", value: true}
        };

        var opts = {
            templateUrl: '/public/pages/template/dialog/mozambique-dialog.html',
            controller: function ($scope, dialog) {

                $scope.dialogClose = function (result) {
                    dialog.close(result);
                };

                $scope.dialogOptions = _.extend(defaults, overrideOpts);
            }
        };
        var olDialog = $dialog.dialog(opts);
        olDialog.open();
    }
};
