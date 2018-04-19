## Template webapp for running a Kotlin-based webapp

Includes:
* Kotlin build process in a Docker container
* Webpack-based JS build process in a Docker container
* React root App component
* Full client-side hotreload support
* Server-side GraphQL provider in Kotlin
* Client-side Apollo provider for React components
* Aphrodite for inline styling
* Deploy the whole thing on Kubernetes, locally or in the cloud

To run in development:

1. Install Docker for Mac (tested) or Docker for Windows (untested) or, on Linux, Docker and Kubernetes (also untested).
2. Run `make start-dev`
3. Navigate to `localhost` in your browser.
4. Party!

To run in production:

TODO