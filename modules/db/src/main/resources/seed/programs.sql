delete from programs;
insert into programs(code, name, description, budgetingApplies, usesDar, active)
    values
    ('HIV','HIV','HIV',FALSE,FALSE,TRUE),
    ('ESS_MEDS','ESSENTIAL MEDICINES','ESSENTIAL MEDICINES',TRUE,FALSE,TRUE),
    ('TB','TB','TB',FALSE,FALSE,TRUE),
    ('MALARIA','MALARIA','MALARIA',FALSE,FALSE,TRUE);