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

function ReportMessageController($scope, SendMessagesReportAttachment, GetFacilitySupervisors, SettingsByKey, ContactList, SendMessages, ReportingFacilityList, NonReportingFacilityList) {

  var allFacilities = [];
  // get configurations
  SettingsByKey.get({key: 'LATE_RNR_NOTIFICATION_SMS_TEMPLATE'}, function (data) {
    $scope.sms_template = data.settings.value;
  });

  SettingsByKey.get({key: 'LATE_RNR_NOTIFICATION_EMAIL_TEMPLATE'}, function (data) {
    $scope.email_template = data.settings.value;
  });

  SettingsByKey.get({key: 'SMS_ENABLED'}, function (data) {
    $scope.sms_enabled = data.settings.value;
  });

  SettingsByKey.get({key: 'LATE_RNR_SUPERVISOR_NOTIFICATION_EMAIL_TEMPLATE'}, function (data) {
    $scope.email_template_supervisor = data.settings.value;
  });

  $scope.$watch('view.selectedOption', function (value) {
    $scope.facilities = [];

    if (value && value === '1') {
      angular.forEach(allFacilities, function (item) {
        if (item.reported) {
          $scope.facilities.push(item);
        }
      });
    } else if (value && value === '0') {
      angular.forEach(allFacilities, function (item) {
        if (!item.reported) {
          $scope.facilities.push(item);
        }
      });
    }
    else {
      $scope.facilities = allFacilities;
    }

  });

  $scope.showSendEmail = function (facility) {
    $scope.selected_facility = facility;
    ContactList.get({type: 'email', facilityId: facility.id}, function (data) {
      $scope.contacts = data.contacts;
    });
    $scope.show_email = !$scope.show_email;
  };

  $scope.showSendEmailSupervisor = function (facility) {
    $scope.selected_facility = facility;
    GetFacilitySupervisors.get({
      facilityId: facility.id
    }, function (data) {
      $scope.contacts = data.supervisors;
      $scope.attachementCaption = "Attachment: Non reporting facility report for " + $scope.zoneName + ' district';
      var fullReportFilter = angular.extend($scope.$parent.filter, {zone: $scope.zoneid});
      $scope.reportFilter = '/reports/download/non_reporting/PDF?max=10000&' + $.param(fullReportFilter);

    });

    $scope.show_email_supervisor = !$scope.show_email_supervisor;

  };

  $scope.showSendSms = function (facility) {
    $scope.selected_facility = facility;
    ContactList.get({type: 'sms', facilityId: facility.id}, function (data) {
      $scope.contacts = data.contacts;
    });
    $scope.show_sms = !$scope.show_sms;
  };

  $scope.doSend = function () {

    if ($scope.show_sms) {
      $scope.sendSms();
      $scope.show_sms = false;
    }
    else if ($scope.show_email_supervisor) {
      $scope.sendSupervisorEmail();
      $scope.show_email_supervisor = false;
    }
    else {
      $scope.sendFacilityEmail();
      $scope.show_email = false;
    }

    $scope.selected_facility.sent = true;

  };

  $scope.sendSupervisorEmail = function () {

    var messages = constructMessage();

    //Since originally the reporting rate report doesn't have zone parameter
    var filterParamsWithZone = angular.extend($scope.$parent.filter, {zone: $scope.zoneid});
    var emailParam = {
      messages: messages,
      reportKey: 'non_reporting',
      subject: 'Non reporting facilities',
      outputOption: 'xls',
      reportParams: filterParamsWithZone
    };

    SendMessagesReportAttachment.post(emailParam, function () {
      $scope.sent_confirmation = true;
    });
  };

  $scope.sendFacilityEmail = function () {

    var messages = constructMessage();

    SendMessages.post({messages: messages}, function () {
      $scope.sent_confirmation = true;
    });
  };

  var constructMessage = function () {
    // construct the messages here
    var messages = [];

    for (var i = 0; i < $scope.contacts.length; i++) {
      var template = $scope.show_email_supervisor ? $scope.email_template_supervisor : $scope.email_template;
      var contact = $scope.contacts[i];

      template = template.replace('{name}', contact.name);
      template = template.replace('{facility_name}', $scope.selected_facility.name);
      template = template.replace('{period}', $scope.selected_facility.name);

      messages.push({
        type: 'email',
        facilityId: $scope.selected_facility.id,
        contact: contact.contact,
        message: template
      });
    }
    return messages;
  };


  $scope.ReportingFacilities = function (feature) {
    ReportingFacilityList.get({
      program: $scope.$parent.filter.program,
      period: $scope.$parent.filter.period,
        schedule:$scope.$parent.filter.schedule,
      geo_zone: feature.id
    }, function (data) {
      $scope.facilities = data.facilities;
      $scope.currentFeature = feature;
      NonReportingFacilityList.get({
        program: $scope.$parent.filter.program,
        period: $scope.$parent.filter.period,
          schedule:$scope.$parent.filter.schedule,
        geo_zone: feature.id
      }, function (data) {
        angular.forEach(data.facilities, function (item) {
          $scope.facilities.push(item);
        });
        allFacilities = $scope.facilities;
      });

    });
  };

  function openDialogBox (feature) {
    $scope.show_email = $scope.show_sms = $scope.show_email_supervisor = false;
    $scope.zoneid = feature.id;
    $scope.zoneName = feature.name;
    $scope.ReportingFacilities(feature);
    $scope.title = 'Facilities in ' + feature.name;
    $scope.$parent.successModal = true;
  }

  $scope.$on('openDialogBox',function(){
    openDialogBox($scope.$parent.currentFeature);
  });




}
