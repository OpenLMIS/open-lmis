delete from programs;
insert into programs(code, name, description, budgetingApplies, usesDar, active, templateConfigured)
    values
    ('HIV','HIV','HIV',FALSE,FALSE,TRUE, FALSE),
    ('ESS_MEDS','ESSENTIAL MEDICINES','ESSENTIAL MEDICINES',TRUE,FALSE,TRUE, FALSE),
    ('TB','TB','TB',FALSE,FALSE,TRUE, FALSE),
    ('MALARIA','MALARIA','MALARIA',FALSE,FALSE,TRUE, FALSE);