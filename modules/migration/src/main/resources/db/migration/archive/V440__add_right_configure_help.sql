delete from rights where name = 'CONFIGURE_HELP_CONTENT';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('CONFIGURE_HELP_CONTENT','ADMIN','right.admin.help.content','Permission to Configure Help Content');