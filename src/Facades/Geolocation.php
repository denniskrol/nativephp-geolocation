<?php

namespace Denniskrol\NativePHPGeolocation\Facades;

use Illuminate\Support\Facades\Facade;

/**
 * @method static array getCurrentPosition(bool $highAccuracy = true)
 * @method static array getBackgroundPosition(bool $highAccuracy = true)
 * @method static array requestPermission()
 * @method static array hasBackgroundPermission()
 * @method static array requestBackgroundPermission()
 */
class Geolocation extends Facade
{
    protected static function getFacadeAccessor(): string
    {
        return 'denniskrol.nativephp-geolocation';
    }
}
