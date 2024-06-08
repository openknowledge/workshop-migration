# Workshop Migration

Willkommen zum Workshop Migration.

## Bauen und starten der Anwendung

Die Anwendung wird mit Docker Compose gebaut und gestartet:

```
docker compose up --build
```

## Aufrufen der Anwendung

Die Anwendung kann unter [Online Shop](http://localhost/) aufgerufen werden.

## Zugriff auf die Datenbank

Unter [Adminer](http://localhost:8080) steht ein Tool zur Verfügung,
um in die Datenbank zu schauen.

| Feld             | Wert                      |
|------------------|---------------------------|
| Datenbank System | PostgreSQL                |
| Server           | online-shop-database:5432 |
| Benutzer         | postgres                  |
| Password         | P@55w0rd                  |
| Datenbank        | postgres                  |

## Aufgabe

Die Datei `checkout.spec.ts` im Ordner `golden-master-tests/tests`
enthält einen UI-Test mit dem Framework Playwright.
Führen Sie den Test aus und schauen Sie, dass er grün ist.
Überlegen Sie, welche weiteren Pfade durch den Bestellabschluss es geben könnte.
Schreiben Sie Tests für diese Fälle.
Schreiben Sie auch Tests für das Pflegen der Adressen in der Kundenverwaltung.

## Tests ausführen

### Lokal

```
cd golden-master-tests
npm install
npx playwright install --with-deps chromium
npm test
```

Die Playwright UI lässt sich lokal folgendermaßen starten:

```
npm run test:ui
```

### GitHub Codespaces

Im Codespace sind alle Abhängigkeiten bereits installiert. Die Playwright VS Code Extension
ist vorinstalliert und ermöglicht das Ausführen und Debuggen der Tests direkt im Editor.

Alternativ lässt sich die Playwright UI über das Terminal starten:

```
cd golden-master-tests
npm run test:ui:codespaces
```

Anschließend öffnet GitHub Codespaces automatisch einen Dialog zum Weiterleiten von Port `8832`.
Die Playwright UI ist dann über den angezeigten Link im Browser erreichbar.
