-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

INSERT INTO rights (name, adminRight, description) VALUES
 ('UPLOADS',TRUE,'Permission to upload'),
 ('UPLOAD_REPORT',TRUE,'Permission to upload reports'),
 ('MANAGE_FACILITY',TRUE,'Permission to manage facilities(crud)'),
 ('MANAGE_ROLE',TRUE,'Permission to create and edit roles in the system'),
 ('MANAGE_SCHEDULE',TRUE,'Permission to create and edit schedules in the system'),
 ('CONFIGURE_RNR',TRUE,'Permission to create and edit r&r template for any program'),
 ('CREATE_REQUISITION',FALSE,'Permission to create, edit, submit and recall requisitions'),
 ('APPROVE_REQUISITION',FALSE,'Permission to approve requisitions'),
 ('AUTHORIZE_REQUISITION',FALSE,'Permission to edit, authorize and recall requisitions'),
 ('MANAGE_USERS',TRUE,'Permission to create and view users'),
 ('CONVERT_TO_ORDER',TRUE,'Permission to convert requisitions to order'),
 ('VIEW_ORDER',TRUE,'Permission to view orders'),
 ('VIEW_REQUISITION',FALSE,'Permission to view requisition'),
 ('VIEW_REPORTS',TRUE,'Permission to view reports'),
 ('MANAGE_REPORTS',TRUE,'Permission to manage reports'),
 ('MANAGE_PROGRAM_PRODUCT',TRUE,'Permission to manage program products'),
 ('MANAGE_DISTRIBUTION',FALSE,'Permission to manage an distribution'),
 ('MANAGE_REGIMEN_TEMPLATE',TRUE,'Permission to manage a regimen template');
