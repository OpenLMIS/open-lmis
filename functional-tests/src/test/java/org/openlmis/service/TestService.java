package org.openlmis.service;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.servicelayerutils.ServiceUtils;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;


@TransactionConfiguration(defaultRollback=true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class TestService extends TestCaseHelper {

    @Test
    public void testService() {
        try{

        ServiceUtils serviceUtils=new ServiceUtils();


        String navigationMenuItems=serviceUtils.postJSON("j_username=Admin123&j_password=Admin123","http://localhost:9091/j_spring_security_check") ;
        System.out.println("Navigation menu : "+navigationMenuItems);

        String facilityList=serviceUtils.getJSON("http://localhost:9091/admin/facilities.json");
        System.out.println("Facility list : "+facilityList);

        String logout=  serviceUtils.getJSON("http://localhost:9091//j_spring_security_logout");
        System.out.println("Logout : "+logout);

         serviceUtils.getJSON("http://localhost:9091//j_spring_security_logout");


        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }


    }






