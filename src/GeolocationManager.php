<?php

namespace Denniskrol\NativePHPGeolocation;

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

    public function getBackgroundPosition(bool $highAccuracy = true): mixed
    {
        if (! function_exists('nativephp_call')) {
            return [
                'success' => false,
                'message' => 'NativePHP bridge helper not available.',
            ];
        }

        return nativephp_call('Geolocation.GetBackgroundPosition', json_encode([
            'highAccuracy' => $highAccuracy,
        ]));
    }

    public function hasBackgroundPermission(): mixed
    {
        if (! function_exists('nativephp_call')) {
            return [
                'granted' => false,
                'message' => 'NativePHP bridge helper not available.',
            ];
        }

        return nativephp_call('Geolocation.HasBackgroundPermission', json_encode([]));
    }

    public function requestBackgroundPermission(): mixed
    {
        if (! function_exists('nativephp_call')) {
            return [
                'granted' => false,
                'message' => 'NativePHP bridge helper not available.',
            ];
        }

        return nativephp_call('Geolocation.RequestBackgroundPermission', json_encode([]));
    }
}
