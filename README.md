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

Führen Sie den Golden Master Test aus.
Sie werden feststellen, dass er fehlschlägt.
Was ist das Problem?

Analysieren Sie den Unterschied in der Tabelle `tab_order` zu der monolithischen Variante.

Beheben Sie das Problem.
