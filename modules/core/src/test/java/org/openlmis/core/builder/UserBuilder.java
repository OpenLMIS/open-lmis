package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.User;

import static com.natpryce.makeiteasy.Property.newProperty;

public class UserBuilder {

    public static final Property<User, String> userName = newProperty();
    public static final Property<User, String> role = newProperty();
    public static final Property<User, Integer> facilityId = newProperty();

    public static final Instantiator<User> defaultUser = new Instantiator<User>() {

        @Override
        public User instantiate(PropertyLookup<User> lookup) {
            User user = new User();
            user.setUserName(lookup.valueOf(userName, "User123"));
            user.setPassword("password");
            user.setRole(lookup.valueOf(role, "USER"));
            user.setFacilityId(lookup.valueOf(facilityId, 9999));
            return user;
        }
    };
}
