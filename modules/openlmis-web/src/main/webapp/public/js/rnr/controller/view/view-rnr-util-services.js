services.factory('downloadPdfService', function (FeatureToggleService, $window) {
    var init = function (_scope_, rnr_id) {
        var toggleKey = {key: 'download.pdf'};
        FeatureToggleService.get(toggleKey, function (result) {
            if (result.key) {
                _scope_.downloadPdf = function () {
                    $window.location.href = "/requisitions/" + rnr_id + "/pdf";
                };
                $(".btn-download-pdf").show();
            }
        });
    };
    return {"init": init};
});

services.factory('downloadSimamService', function (FeatureToggleService, $window) {
    var init = function (_scope_, rnr_id) {
        var toggleKey = {key: 'download.simam'};
        FeatureToggleService.get(toggleKey, function (result) {
            if (result.key) {
                _scope_.downloadSimam = function () {
                    $window.location.href = "/requisitions/" + rnr_id + "/simam";
                };
                $(".btn-download-simam").show();
            }
        });
    };
    return {"init": init};
});