# LocationApp

An Android app built around an interactive map: it shows your current position, lets you drop and manage location **pins** (long-press on the map), and tap pins to see details in a bottom sheet. Pin data is stored on the device.

**Status:** work in progress — APIs, UX, and features may change.

## Tech stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose, Material 3  
- **Map:** [MapLibre](https://maplibre.org/) (MapLibre Compose), raster tiles (e.g. OpenFreeMap “Liberty” style)  
- **Location:** Google Play services Location API  
- **Local storage:** Room  
- **Architecture / DI:** Hilt, coroutines, lifecycle-aware components  


---

*Private / early-stage project.*
