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

Die Datei `CheckoutTest` im Ordner 
`golden-master-tests/src/test/java/de/openknowledge/sample/onlineshop/test`
enthält einen UI-Test mit dem Framework Playwright.
Führen Sie den Test aus und schauen Sie, dass er grün ist.
Überlegen Sie, welche weiteren Pfade durch den Bestellabschluss es geben könnte.
Schreiben Sie Tests für diese Fälle.
Schreiben Sie auch Tests für das Pflegen der Adressen in der Kundenverwaltung.
