DO $$ 
BEGIN 
    BEGIN
        BEGIN
          INSERT INTO manufacturers ( NAME, website, contactperson, primaryphone, email, description, specialization, geographiccoverage, registrationdate, createdby, createddate, modifiedby, modifieddate )
          VALUES ( 'Novartis Vaccines', 'www.novartisvaccines.com/us/index.shtml', null, null, null, 'Vaccine manufacturer', 'Vaccine', 'World-wide', null, 1, now(), 1, now() );
        EXCEPTION
            WHEN unique_violation THEN RAISE NOTICE 'Novartis Vaccines already exists';
        END;
    END;
    BEGIN
        BEGIN
         INSERT INTO manufacturers ( NAME, website, contactperson, primaryphone, email, description, specialization, geographiccoverage, registrationdate, createdby, createddate, modifiedby, modifieddate )
         VALUES ( 'Novartissanofi Pasteur', 'www.vaccineshoppe.com', null, null, null, 'Vaccine manufacturer', 'Vaccine', 'World-wide', null, 1, now(), 1, now() );
        EXCEPTION
            WHEN unique_violation THEN RAISE NOTICE 'Novartissanofi Pasteur already exists';
        END;
    END;
    BEGIN
        BEGIN
            INSERT INTO manufacturers ( NAME, website, contactperson, primaryphone, email, description, specialization, geographiccoverage, registrationdate, createdby, createddate, modifiedby, modifieddate )
            VALUES ( 'Pfizer', 'www.pfizerpro.com/ ', null, null, null, 'Vaccine manufacturer', 'Vaccine', 'World-wide', null, 1, now(), 1, now() );
        EXCEPTION
             WHEN unique_violation THEN RAISE NOTICE 'Pfizer already exists';
        END;
    END;
    BEGIN
        BEGIN
         INSERT INTO manufacturers (NAME, website, contactperson, primaryphone, email, description, specialization, geographiccoverage, registrationdate, createdby, createddate, modifiedby, modifieddate )
         VALUES ( 'GSK', '', null, null, null, 'Vaccine manufacturer', 'Vaccine', 'World-wide', null, 1, now(), 1, now() );
       EXCEPTION
            WHEN unique_violation THEN RAISE NOTICE 'GSK already exists';
        END;
    END; 
END;
$$