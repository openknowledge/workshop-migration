# Workshop Migration

Willkommen zum Workshop Migration.

## Bauen und starten der Anwendung

Die Anwendung wird mit Docker Compose gebaut und gestartet:

```
docker compose up --build
```

## Aufrufen der Anwendung

Die Anwendung kann unter [Online Shop](http://localhost:4000/index.html) aufgerufen werden.

## Zugriff auf die Datenbank

Unter [Adminer](http://localhost:8080) steht ein Tool zur Verfügung,
um in die Datenbank zu schauen.

| Datenbank System | PostgreSQL                |
| Server           | online-shop-database:5432 |
| Benutzer         | postgres                  |
| Password         | P@55w0rd                  |
| Datenbank        | postgres                  |

## Aufgabe

Navigieren Sie zum Warenkorb und schauen Sie sich dann den Inhalt der Tabelle `tab_order` an.
Navigieren Sie durch den Checkout-Prozess und sehen Sie, wie sich der Inhalt der Tabelle ändert.
