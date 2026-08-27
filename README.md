# Projectmata Mobile Geolocation

[![Latest Version](https://img.shields.io/packagist/v/denniskrol/nativephp-geolocation.svg)](https://packagist.org/packages/denniskrol/nativephp-geolocation)
[![Total Downloads](https://img.shields.io/packagist/dt/denniskrol/nativephp-geolocation.svg)](https://packagist.org/packages/denniskrol/nativephp-geolocation)
[![License](https://img.shields.io/packagist/l/denniskrol/nativephp-geolocation.svg)](https://packagist.org/packages/denniskrol/nativephp-geolocation)

Geolocation plugin for [NativePHP Mobile](https://nativephp.com). Reads the device's current position on Android and iOS with a single call.

## Requirements

- PHP `^8.4`
- Laravel `^11.0` or `^12.0` / `^13.0`
- `nativephp/mobile`
- Android: `min_version 33`, depends on `com.google.android.gms:play-services-location:21.3.0`
- iOS: `min_version 18.2`

## Installation

```bash
composer require denniskrol/nativephp-geolocation
```

Rebuild the mobile app so NativePHP wires up the native plugin and permissions:

```bash
php artisan native:run android
# or
php artisan native:run ios
```

## Permissions

The package declares these for you via its `nativephp.json` manifest — you don't need to add them manually:

- **Android** — `ACCESS_COARSE_LOCATION`, `ACCESS_FINE_LOCATION`, `ACCESS_BACKGROUND_LOCATION`
- **iOS** — `NSLocationWhenInUseUsageDescription` is set to *"This app uses your location to show your current position."* Override it in your app's `info_plist` config if you want different copy.

Always call `requestPermission()` before requesting a position, and handle the case where the user denies.

For Android background work, request foreground permission while the app is open, then call
`requestBackgroundPermission()`. Android 11 and later opens the app's settings screen because
the user must explicitly choose **Allow all the time** there. Background jobs must never try to
display permission prompts.

## Usage

### PHP (Laravel)

```php
use Denniskrol\NativePHPGeolocation\Facades\Geolocation;

// Ask the OS for location permission (no-op if already granted)
$perm = Geolocation::requestPermission();

// Then read the position
$pos = Geolocation::getCurrentPosition(highAccuracy: true);

// $pos = ['success' => true, 'latitude' => ..., 'longitude' => ..., 'accuracy' => ...]

// While the app is open, open Android's permission settings when needed.
$background = Geolocation::requestBackgroundPermission();

// Use this from a background runtime after background permission is granted.
$pos = Geolocation::getBackgroundPosition(highAccuracy: false);
```

### JavaScript (in-app)

The plugin registers itself on `window.NativePHP.Geolocation`:

```js
await window.NativePHP.Geolocation.RequestPermission();

const pos = await window.NativePHP.Geolocation.GetCurrentPosition({ highAccuracy: true });

if (pos.success) {
    console.log(pos.latitude, pos.longitude, pos.accuracy);
}
```

Or as a bundled import:

```js
import Geolocation, { getCurrentPosition } from 'denniskrol-nativephp-geolocation';
```

## Bridge methods

| Method                            | Params                       | Returns                                                    |
| --------------------------------- | ---------------------------- | ---------------------------------------------------------- |
| `Geolocation.RequestPermission`   | —                            | `{ success, granted }`                                     |
| `Geolocation.GetCurrentPosition`  | `{ highAccuracy: boolean }`  | `{ success, latitude, longitude, accuracy, timestamp }`    |
| `Geolocation.RequestBackgroundPermission` | —                     | `{ granted, message }`                                      |
| `Geolocation.HasBackgroundPermission` | —                       | `{ granted }`                                               |
| `Geolocation.GetBackgroundPosition` | `{ highAccuracy: boolean }` | Position or `BACKGROUND_LOCATION_PERMISSION_DENIED` error   |

`getCurrentPosition()` works with either an Android activity or application context and uses an
active Fused Location request. `getBackgroundPosition()` additionally requires Android background
location permission, making it appropriate for scheduler and job integrations without coupling this
package to any particular background-task plugin. The `highAccuracy` flag maps to Fused Location's
high-accuracy mode on Android and `kCLLocationAccuracyBest` on iOS.

## License

MIT
