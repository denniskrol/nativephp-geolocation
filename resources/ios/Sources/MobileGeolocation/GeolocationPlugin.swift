import Foundation
import CoreLocation

public final class GeolocationPlugin: NSObject, CLLocationManagerDelegate {
    private let locationManager = CLLocationManager()
    private var latestResult: [String: Any] = [:]

    public override init() {
        super.init()
        locationManager.delegate = self
    }

    public func requestPermission() -> [String: Any] {
        locationManager.requestWhenInUseAuthorization()
        return [
            "granted": false,
            "message": "Permission requested"
        ]
    }

    public func getCurrentPosition(highAccuracy: Bool = true) -> [String: Any] {
        if highAccuracy {
            locationManager.desiredAccuracy = kCLLocationAccuracyBest
        } else {
            locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters
        }

        locationManager.requestLocation()

        if let location = locationManager.location {
            return [
                "success": true,
                "latitude": location.coordinate.latitude,
                "longitude": location.coordinate.longitude,
                "accuracy": location.horizontalAccuracy,
                "timestamp": location.timestamp.timeIntervalSince1970
            ]
        }

        return [
            "success": false,
            "message": "Location unavailable"
        ]
    }

    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        //
    }

    public func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        //
    }
}
