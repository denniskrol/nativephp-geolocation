<?php

namespace Denniskrol\NativePHPGeolocation;

use Illuminate\Support\ServiceProvider;

class NativePHPGeolocationServiceProvider extends ServiceProvider
{
    public function register(): void
    {
        $this->app->singleton('denniskrol.nativephp-geolocation', function () {
            return new GeolocationManager();
        });
    }

    public function boot(): void
    {
        //
    }
}
