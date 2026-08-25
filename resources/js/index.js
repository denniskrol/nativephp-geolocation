const baseUrl = '/_native/api/call';

console.log('[GEO-BRIDGE] index.js loaded');

function debugLog(...args) {
    console.log('[GEO-BRIDGE]', ...args);
}

async function bridgeCall(method, params = {}) {
    debugLog('bridgeCall:start', { method, params, baseUrl });

    const response = await fetch(baseUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
        },
        body: JSON.stringify({
            method,
            params,
        }),
    });

    debugLog('bridgeCall:response-status', response.status);

    let data = null;

    try {
        data = await response.json();
        debugLog('bridgeCall:response-json', data);
    } catch (e) {
        debugLog('bridgeCall:invalid-json', e);
        throw new Error(`Native bridge returned invalid JSON for ${method}`);
    }

    if (!response.ok) {
        debugLog('bridgeCall:response-not-ok', data);
        throw new Error(data?.message || `Native bridge error: ${response.status}`);
    }

    return data;
}

export async function getCurrentPosition(highAccuracy = true) {
    debugLog('getCurrentPosition called', { highAccuracy });
    return bridgeCall('Geolocation.GetCurrentPosition', { highAccuracy });
}

export async function requestPermission() {
    debugLog('requestPermission called');
    return bridgeCall('Geolocation.RequestPermission', {});
}

export async function getBackgroundPosition(highAccuracy = true) {
    debugLog('getBackgroundPosition called', { highAccuracy });
    return bridgeCall('Geolocation.GetBackgroundPosition', { highAccuracy });
}

export async function hasBackgroundPermission() {
    debugLog('hasBackgroundPermission called');
    return bridgeCall('Geolocation.HasBackgroundPermission', {});
}

export async function requestBackgroundPermission() {
    debugLog('requestBackgroundPermission called');
    return bridgeCall('Geolocation.RequestBackgroundPermission', {});
}

const Geolocation = {
    GetCurrentPosition(options = {}) {
        const highAccuracy =
            typeof options === 'boolean'
                ? options
                : (options?.highAccuracy ?? true);

        debugLog('Geolocation.GetCurrentPosition wrapper', { options, highAccuracy });
        return getCurrentPosition(highAccuracy);
    },

    RequestPermission() {
        debugLog('Geolocation.RequestPermission wrapper');
        return requestPermission();
    },

    GetBackgroundPosition(options = {}) {
        const highAccuracy =
            typeof options === 'boolean'
                ? options
                : (options?.highAccuracy ?? true);

        debugLog('Geolocation.GetBackgroundPosition wrapper', { options, highAccuracy });
        return getBackgroundPosition(highAccuracy);
    },

    HasBackgroundPermission() {
        debugLog('Geolocation.HasBackgroundPermission wrapper');
        return hasBackgroundPermission();
    },

    RequestBackgroundPermission() {
        debugLog('Geolocation.RequestBackgroundPermission wrapper');
        return requestBackgroundPermission();
    },
};

if (typeof window !== 'undefined') {
    window.NativePHP = window.NativePHP || {};
    window.NativePHP.Geolocation = Geolocation;
    debugLog('window.NativePHP.Geolocation registered', window.NativePHP.Geolocation);
} else {
    debugLog('window is undefined');
}

export default Geolocation;
