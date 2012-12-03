delete from program;
insert into program(code, name, description, budgeting_applies, uses_dar, active)
    values
    ('HIV','HIV','HIV',FALSE,FALSE,TRUE),
    ('ARV','ARV','ARV',FALSE,FALSE,TRUE),
    ('ESS_MEDS','ESSENTIAL MEDICINES','ESSENTIAL MEDICINES',TRUE,FALSE,TRUE),
    ('VACCINES','VACCINES','VACCINES',FALSE,FALSE,TRUE),
    ('TB','TB','TB',FALSE,FALSE,TRUE),
    ('MALARIA','MALARIA','MALARIA',FALSE,FALSE,TRUE),
    ('SMALL_POX','SMALL POX','SMALL POX',FALSE,FALSE,FALSE);