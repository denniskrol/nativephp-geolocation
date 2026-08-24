<?php

namespace Projectmata\MobileGeolocation;

class GeolocationManager
{
    public function getCurrentPosition(bool $highAccuracy = true): mixed
    {
        if (! function_exists('nativephp_call')) {
            return [
                'success' => false,
                'message' => 'NativePHP bridge helper not available.',
            ];
        }

        return nativephp_call('Geolocation.GetCurrentPosition', json_encode([
            'highAccuracy' => $highAccuracy,
        ]));
    }

    public function requestPermission(): mixed
    {
        if (! function_exists('nativephp_call')) {
            return [
                'success' => false,
                'message' => 'NativePHP bridge helper not available.',
            ];
        }

        return nativephp_call('Geolocation.RequestPermission', json_encode([]));
    }
}
