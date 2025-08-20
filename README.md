## HexVault (Android)

Non-custodial hot wallet for Bitcoin testnet, built with Kotlin and Jetpack Compose. Keys never leave the device; transactions are signed locally and broadcast via public APIs.

### Features

- Non-custodial design (seed and keys on the device)
- BIP-39 seed generation; seed is shown once on onboarding
- Secure local storage via Android Keystore + EncryptedSharedPreferences (AES-256)
- BTC testnet wallet (BIP-84, P2WPKH bech32, derivation path m/84'/1'/0'/0/0) powered by bitcoinj
- Address display with QR code and copy-to-clipboard
- UTXO fetch and confirmed balance via Blockstream testnet API
- Manual UTXO selection, fee rate input (sats/vB), and automatic fee estimation
- Local transaction build and sign (P2WPKH), broadcast via Blockstream
- BTC/USD price via CoinGecko Simple Price API (good for MVPs)
- MVVM + Hilt DI + Jetpack Compose UI
- Localization: English (default), Ukrainian, Polish

### Screens (MVP)

- Onboarding: generate and display BIP-39 seed (one-time)
- Main: price ticker, address, balance, QR, send form (recipient, amount, fee rate, UTXO selection)
- Settings: language selection (EN/UK/PL)

### Security model

- Seed and private keys are never stored in plaintext; they are encrypted with a per-device master key (Android Keystore)
- Seed is shown once; users are instructed to back it up offline
- Private keys never leave the device; all signatures happen locally
- Only signed transactions are sent out (broadcast); no secrets are transmitted to servers
- This repository targets testnet; do not use for mainnet funds without a thorough audit

### Tech stack

- Kotlin, Jetpack Compose, Material 3
- Hilt (DI), MVVM (ViewModel + StateFlow)
- bitcoinj (BIP-39/BIP-84, testnet)
- Retrofit + OkHttp + Moshi (with KotlinJsonAdapterFactory)

### Public APIs used

- CoinGecko: Simple Price endpoint for BTC/USD. See documentation: [CoinGecko – Simple Price](https://docs.coingecko.com/reference/simple-price)
- Blockstream testnet API: UTXO listing and transaction broadcast
  - Base: `https://blockstream.info/testnet/`
  - Examples: `GET /api/address/{address}/utxo`, `POST /api/tx`

Notes:
- Free public endpoints are used by default. For higher rate limits or stability, consider API keys or self-hosted infrastructure.

### Getting started

Prerequisites:
- Android Studio (Giraffe+ recommended)
- JDK 11
- Android SDK 24+

Build & run:

```bash
./gradlew :app:assembleDebug
```

Then install the APK or run directly from Android Studio on a device/emulator.

### Project structure (high level)

- `app/src/main/java/com/metraakladap/hexvault/`
  - `app/` – application class
  - `crypto/` – seed and wallet managers (BIP-39/BIP-84, signing)
  - `data/` – UI state models
  - `di/` – Hilt modules (network, app)
  - `network/` – Retrofit APIs (CoinGecko, Blockstream)
  - `repository/` – price and wallet repositories
  - `screens/` – Compose screens (onboarding, main, settings)
  - `ui/components/` – shared UI (gradient background, cards, QR)
  - `viewmodel/` – view models (Loading, Main, Onboarding, Settings)

### Wallet behavior (MVP)

- Seed generation: 128-bit entropy -> BIP-39 mnemonic (testnet)
- Address derivation: BIP-84 path m/84'/1'/0'/0/0 (bech32 P2WPKH)
- Balance: fetched via Blockstream (confirmed UTXO sum)
- Send flow:
  - Select UTXO(s), set fee rate (sats/vB)
  - Fee is estimated with a P2WPKH heuristic (inputs≈68 vB each; outputs≈31 vB each + overhead)
  - Build P2WPKH transaction, sign locally, broadcast via Blockstream

### Configuration

- The app uses public endpoints; no API keys required for the MVP
- If you want to switch networks/endpoints, check `NetworkModule.kt`

### Limitations (MVP)

- Testnet-only
- Fee estimation is approximate; recommended to integrate a mempool fee estimator
- No coin control policies beyond manual selection
- Not audited; for educational/testing purposes

### Roadmap

- BIP-21 QR (`bitcoin:` URI with amount/label)
- Advanced fee estimation (mempool/vbytes), RBF, and change output sizing
- Mainnet toggle and safety checks
- Hardware wallet (air-gapped QR) flow: unsigned tx -> sign on cold device -> import
- SQLCipher for encrypted local DB backups
- App-wide locale persistence (DataStore + AppCompatDelegate)

### License

MIT

### Acknowledgements

- CoinGecko API documentation: [Simple Price](https://docs.coingecko.com/reference/simple-price)
- Blockstream testnet explorer and API

### Disclaimer

This project is for educational and testing purposes on Bitcoin testnet. Do not use with real funds without a professional security review and comprehensive testing.


