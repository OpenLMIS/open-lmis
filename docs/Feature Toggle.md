How Feature Toggle was done
---------------------------

Feature toggling is done at build time, with a check for `toggleOnCustom` in the `gradle.properties` file. The 
`build.gradle` file in the `db` module has an extra task called `enableCustom` that only runs if `toggleOnCustom` is set 
to `true`.

The actual implementation of toggling is mostly done by using the existing permissions framework. The UI shows and hides 
certain menu items and pages if the logged in user has the corresponding permission. Features are effectively toggled 
off by removing the applicable permission completely from the system. Since the permission does not exist, the user does 
not have the permission and is not able to interact with the applicable feature.

The settings and tabs in the Other Settings page are shown by getting all of the settings in the 
`configuration_settings` table that have `isConfigurable` to true, grouped by `groupName`. So these settings and tabs 
are toggled off by changing the custom settings `isConfigurable` column to false.

A final setting is toggled off by changing the default page setting back to the home page, and not the custom dashboard.

The build process removes the custom permissions, sets the custom settings to not configurable, and changes the default 
page to the home page, in a file called `disable_custom.sql`. The enableCustom task re-adds these permissions, sets the 
custom settings to configurable again, and changes the home page back to the dashboard page, in a file called 
`enable_custom.sql`.

Extending Feature Toggle
------------------------

For minor changes in toggling UI items, new permissions should be created in the seed, toggled off in the 
`disable_custom.sql` script, toggled back on in the `enable_custom.sql` script, and checked for in the HTML.

For more extensive changes, the toggling mechanism can be copied for how to toggle at build time.