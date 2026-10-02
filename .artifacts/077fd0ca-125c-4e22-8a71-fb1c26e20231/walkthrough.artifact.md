# Walkthrough - Payment Sandbox & Release Build

I have implemented the simulated payment sandbox features and generated a fresh release APK for you.

## New Features: Payment Sandbox

I’ve added a realistic, multi-step payment simulation to the subscription flow:

### 1. Payment Selection Screen
Users can now choose between **MasterCard** and **PayPal** as their payment method after selecting "Payment Options".

### 2. Mock Forms
- **MasterCard**: A form with fields for Card Number (16 digits), Expiry (MM/YY), and CVV.
- **PayPal**: A form for PayPal Email and Password.
*Note: These are for simulation only; no real data is processed or stored.*

### 3. Processing Simulation
When a user clicks "Pay" or "Login", the app switches to a **"Processing Payment..."** screen with a loading spinner for 3 seconds. This mimics a real network transaction.

### 4. Success State
Once processed, the app shows a "Payment Successful!" message and unlocks the VPN server selection screen permanently.

## Release Application

I have generated a new signed **Release APK** that includes all these features.

> [!IMPORTANT]
> **APK Path**: [app-release.apk](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/build/outputs/apk/release/app-release.apk)

### Command to Search for the APK:
Use this command in your terminal to quickly find the file location:
```powershell
dir /s app-release.apk
```

## How to Test the Flow
1. **Login**: Use `Ndumiso` / `Ndumiso1`.
2. **Subscription**: Tap "Payment Options".
3. **Select**: Choose "MasterCard".
4. **Enter Data**: Type any 16 digits and click "Pay Now".
5. **Watch**: The "Processing" spinner will appear for 3 seconds, then you'll be connected to the Server Selection screen!
