# Implementation Plan - Add Server Selection and More IP Addresses

The goal is to allow users to select a VPN server from a list of 10+ countries instead of relying on random selection. We will add a settings button to trigger this selection and expand the available server list.

## Proposed Changes

### [app module](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/java/com/example/SkyNetVpnService/MainActivity.kt)
- Expand `mockServers` list with 10 additional worldwide locations.
- Add a listener for the new settings button.
- Implement an `AlertDialog` to show the list of servers and update the selection.
- Update the connection logic to use the selected server.

#### [MODIFY] [activitymain.xml](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/res/layout/activitymain.xml)
- Add an `ImageButton` or `Button` for "Settings" / "Change Server".
- Ensure the layout remains consistent and accessible.

#### [NEW] [ic_settings.xml](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/res/drawable/ic_settings.xml)
- Add a vector drawable for the settings icon if not already present.

#### [NEW] [round_button_bg.xml](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/res/drawable/round_button_bg.xml)
- Add the missing background drawable for the connect button (to ensure build success and proper UI).

#### [NEW] [ic_vpn_foreground.xml](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/res/drawable/ic_vpn_foreground.xml)
- Add a placeholder vector drawable for the VPN status icon if not already present.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify that the project builds without errors.

### Manual Verification
- Deploy the app to a device/emulator.
- Tap the new "Settings" button.
- Verify that a list of 14 countries (4 original + 10 new) appears.
- Select a country and verify that the "Server: ..." text updates.
- Tap "Connect" and verify that it connects to the selected server.
