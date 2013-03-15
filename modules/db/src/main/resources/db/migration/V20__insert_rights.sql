INSERT INTO rights (name, adminRight, description) VALUES
 ('UPLOADS',TRUE,'Permission to upload'),
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
 ('VIEW_REQUISITION',FALSE,'Permission to view requisition');
