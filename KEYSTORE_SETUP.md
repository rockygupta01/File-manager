## Keystore Setup (Required for Play Store Release)

Add these lines to local.properties (never commit this file to git):

```
STORE_FILE=../keystore.jks
STORE_PASSWORD=your_store_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

### Generate a keystore (one-time setup):
```bash
keytool -genkey -v \
  -keystore keystore.jks \
  -alias privafiles \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```
Store keystore.jks securely — losing it means you can never update the app on Play Store.
