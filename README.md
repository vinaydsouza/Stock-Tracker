# Stock Tracker

A lightweight Java desktop application for live ticker tracking, market direction status, and grouped watchlists.

## Context

This project is built as a Java Swing application in the `app` module. It fetches stock quotes from Yahoo Finance and shows live-priced tickers in a table. The UI includes group filtering and a market status banner that now recognizes Wall Street open/closed hours.

## Key features

- Live quote refreshes for tracked tickers
- Market direction label with "Market Up", "Market Down", and "Market Closed" states
- Market pauses refresh when Wall Street is closed
- Group-based ticker filtering
- Add and track tickers by symbol with a compact suggestion dropdown
- Right-click group menu for rename, refresh, and delete actions

## How to run

From the `Stock Tracker` root directory:

```bash
./gradlew run
```

Or build the app with:

```bash
./gradlew build
```

## Notes

- The repository has been initialized with `git` and pushed to `https://github.com/vinaydsouza/Stock-Tracker.git`
- Author identity for the commit is configured with GitHub no-reply email mode

## Project structure

- `app/src/main/java/stock/tracker` - application source code
- `app/build.gradle` - module build configuration
- `gradle` - wrapper and version configuration
- `gradlew` / `gradlew.bat` - Gradle wrapper scripts
- `run-stock-tracker.sh` - helper launcher script

## License

This project does not currently include an explicit license file.
