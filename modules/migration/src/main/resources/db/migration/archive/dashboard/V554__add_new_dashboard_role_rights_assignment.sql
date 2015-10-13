DELETE FROM Rights where name='ACCESS_NEW_DASHBOARD';
INSERT INTO Rights values('ACCESS_NEW_DASHBOARD', 'REPORT', 'Permission to access new dashboard', NOW(), 13, 'right.dashboard');
