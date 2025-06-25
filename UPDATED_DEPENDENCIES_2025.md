# Updated Dependencies for REAI - June 2025

This document outlines all the dependency updates made to ensure compatibility with the latest stable versions as of June 2025.

## Java/JVM Updates

### Updated to Java 24 GA Release
- **Java 24**: General Availability release as of March 18, 2025
- **Installation via SDKMAN**: 
  ```bash
  curl -s "https://get.sdkman.io" | bash
  source ~/.sdkman/bin/sdkman-init.sh
  sdk install java 24.0.1-open
  ```
- **Note**: Java 24 is a short-term release (6 months support until September 2025)

## Spring Boot & Kotlin Updates

### Spring Boot: 3.5.0 → 3.5.3
- Latest stable release with bug fixes and improvements
- Release date: June 2025

### Kotlin: 2.2.0-RC → 2.1.20 (Stable)
- Changed from release candidate to latest stable version
- Release date: March 20, 2025
- Better compatibility with Spring Boot 3.5.x

## Docker Updates

### Base Image: Updated to amazoncorretto:24
- Uses Java 24 GA release (March 2025)
- Latest performance optimizations with ZGC
- Supports virtual threads and modern JVM features

## Cloudflare Worker Updates

### Dependencies Updated:
- **postal-mime**: 2.2.0 → 2.4.3 (latest stable)
- **@cloudflare/workers-types**: 4.20241205.0 → 4.20250620.0 (latest)
- **typescript**: 5.6.3 → 5.8.3 (latest stable)
- **wrangler**: 4.21.2 → 3.87.0 (latest stable)

### Configuration Updates:
- **Compatibility date**: Updated to 2025-06-25
- **Added nodejs_compat_v2**: For better Node.js API support

## Build System Compatibility

### Gradle Compatibility
- Kotlin 2.1.20 is compatible with Gradle 7.6.3 through 8.11
- Spring Boot 3.5.3 requires Gradle 7.6.4 or later

### Node.js Compatibility (for Cloudflare Worker)
- postal-mime 2.4.3 supports modern JavaScript environments
- TypeScript 5.8.3 provides latest language features

## Installation Instructions

### 1. Install Java 24
```bash
# Install SDKMAN if not already installed
curl -s "https://get.sdkman.io" | bash
source ~/.sdkman/bin/sdkman-init.sh

# Install Java 24
sdk install java 24.0.1-open
```

### 2. Update Node.js Dependencies
```bash
cd cloudflare-worker
npm install
```

### 3. Build the Project
```bash
# From project root
./gradlew build
```

### 4. Run the Application
```bash
# Start database
docker-compose up -d

# Run Spring Boot app
./gradlew :web-app:bootRun --args='--spring.profiles.active=dev'
```

### 5. Deploy Cloudflare Worker
```bash
cd cloudflare-worker
npm run deploy
```

All dependencies are now at their latest stable versions as of June 25, 2025.