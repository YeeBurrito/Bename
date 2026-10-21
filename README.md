# Bename

An Android app for remembering people's names. Save the people you meet, group them with tags, attach places to those tags, and get a reminder of who you might run into when you arrive somewhere.

Built with Kotlin, Jetpack Compose, Room, Hilt, and the Google Places SDK.

## Building

1. Open the project in Android Studio (JDK 17).
2. Create a `secrets.properties` file in the project root with your own Google Places API key:

   ```properties
   PLACES_API_KEY=your_key_here
   ```

   This file is git-ignored. Without it the app still builds, using the placeholder in `local.defaults.properties`, but location search won't work.
3. Run the `app` configuration.
