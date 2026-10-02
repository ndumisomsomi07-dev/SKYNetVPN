# Implementation Plan - Simulated Payment Flow (Sandbox)

Add a realistic-looking, simulated (sandbox) payment flow for MasterCard and PayPal to the VPN subscription process.

## Proposed Changes

### 1. New UI Layouts

#### [NEW] [layout_payment_selection.xml](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/res/layout/layout_payment_selection.xml)
- Buttons for **MasterCard** and **PayPal**.

#### [NEW] [layout_mastercard_form.xml](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/res/layout/layout_mastercard_form.xml)
- Fields for **Card Number**, **Expiry Date**, and **CVV**.
- "Pay Now" button.

#### [NEW] [layout_paypal_form.xml](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/res/layout/layout_paypal_form.xml)
- Fields for **PayPal Email** and **Password**.
- "Login to PayPal" button.

#### [NEW] [layout_payment_processing.xml](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/res/layout/layout_payment_processing.xml)
- A **ProgressBar** (spinner) and "Processing Payment..." text to simulate activity.

### 2. Layout Integration

#### [MODIFY] [activitymain.xml](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/res/layout/activitymain.xml)
- Add the 4 new layouts to the `ViewFlipper`.
  - Screen 4: Payment Selection
  - Screen 5: MasterCard Form
  - Screen 6: PayPal Form
  - Screen 7: Payment Processing

### 3. Logic & Navigation

#### [MODIFY] [MainActivity.kt](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/java/com/example/SkyNetVpnService/MainActivity.kt)
- **Update Subscription Logic**:
  - `btnFreeTrial` continues to skip to Server Selection.
  - `btnPayment` now navigates to the **Payment Selection** screen.
- **Implement Payment Navigation**:
  - Handle MasterCard selection -> MasterCard Form.
  - Handle PayPal selection -> PayPal Form.
- **Simulate Processing**:
  - When a user clicks "Pay" or "Login" in a payment form, switch to the **Processing** screen.
  - Use a `Handler` to delay for 2-3 seconds to simulate a network call.
  - Once "processed", set the `isSubscribed` flag to `true` in `SharedPreferences` and navigate to **Server Selection**.

## Verification Plan

### Automated Tests
- Run `gradle assembleDebug` to verify no compilation errors.

### Manual Verification
1. **Login**: Authenticate with a valid account.
2. **Subscription Screen**: Click "Payment Options".
3. **Selection**: Choose "MasterCard".
4. **Form**: Enter dummy data (e.g., 1234 5678) and click "Pay Now".
5. **Processing**: Verify the spinner appears for a few seconds.
6. **Success**: Verify it automatically moves to the Server Selection screen.
7. **Repeat for PayPal**: Verify the PayPal flow works similarly.
8. **Persistence**: Close the app after "payment" and ensure it stays on the Server Selection screen when reopened.
