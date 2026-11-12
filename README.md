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
- Search the visible watchlist by ticker, quote, or company name
- Sort quote columns by clicking their headers
- Export the currently visible (group-filtered and searched) tickers to UTF-8 CSV

## How to run

From the `Stock Tracker` root directory:

```bash
./gradlew run
```

Or build the app with:

```bash
./gradlew build
```

## Download

For Ubuntu 24.04 or newer on 64-bit Intel/AMD, download the `stock-tracker_*_amd64.deb` file from the Assets section of the latest [GitHub Release](https://github.com/vinaydsouza/Stock-Tracker/releases) and install it with:

```bash
sudo apt install ./stock-tracker_*_amd64.deb
```

The Debian package includes its Java runtime and adds Stock Tracker to the applications menu.

The release also includes `stock-tracker.zip` for other operating systems. Extract it and run the launcher script in the `bin` directory (`stock-tracker` on Linux/macOS or `stock-tracker.bat` on Windows); Java 21 is required for this ZIP.

Release packages are built automatically when a GitHub Release is published. To build the ZIP locally, run:

```bash
./gradlew :app:distZip
```

The ZIP is created in `app/build/distributions`.

To build the Ubuntu package locally on Ubuntu, run `./gradlew :app:installDist`, then:

```bash
jpackage \
  --type deb \
  --name "Stock Tracker" \
  --app-version 1.0.0 \
  --vendor "Stock Tracker" \
  --description "A lightweight desktop stock watchlist and quote tracker." \
  --linux-deb-maintainer "vinay-dsouza@users.noreply.github.com" \
  --input app/build/install/stock-tracker/lib \
  --main-jar app.jar \
  --main-class stock.tracker.App \
  --dest app/build/distributions \
  --linux-package-name stock-tracker \
  --linux-app-category Finance \
  --linux-menu-group Finance \
  --linux-shortcut
```

The `.deb` package requires `fakeroot` and `dpkg-deb`.

## Notes

- The repository has been initialized with `git` and pushed to `https://github.com/vinaydsouza/Stock-Tracker.git`
- Author identity for the commit is configured with GitHub no-reply email mode

## Project structure

- `app/src/main/java/stock/tracker` - application source code
- `app/build.gradle` - module build configuration
- `gradle` - wrapper and version configuration
- `gradlew` / `gradlew.bat` - Gradle wrapper scripts
- `run-stock-tracker.sh` - helper launcher script

## Spec-Driven Development Docs

- `spec.md` - business layer with user stories, TDD proof, priorities, and standards
- `plan.md` - technical governance, TDD workflow, test layers, compliance, and merge gate strategy
- `tasks.md` - phased work breakdown with numbered checkbox tasks and test-first tasks
- `governance.md` - code governance, color-coded merge policy, and enforcement rules

## License

This project does not currently include an explicit license file.
