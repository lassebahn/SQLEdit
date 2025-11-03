# SQLEdit

Mit dieser Anwendung können SQL-Abfragen ausgeführt und Daten in relationalen Datenbanken angezeigt werden.

Das Projekt kombiniert **Spring Boot** für Konfiguration & Dependency Injection mit **JavaFX** für die Benutzeroberfläche.  

---

## ✨ Features
- Verbindungsaufbau zu verschiedenen Datenbanken (MS SQL Server, DB2, DB2/400)  
- SQL-Editor  
- Logging über Log4j2  

---

## ⚙️ Voraussetzungen
- **Java 21** oder neuer  
- **Maven 3.9+** (für den Build)  
- Git (zum Klonen des Projekts)  
- Falls MS SQL Server benutzt wird, ist darauf zu achten, dass im Sql Server Configuration Manager unter SQL Server-Netzwerkkonfiguration "TCP/IP" aktiviert ist. Unter Eigenschaften für "TCP/IP" sollte ein fester TCP-Port im Abschnitt IPAll eingetragen sein (z.B. Port 1433). 

---

## 🚀 Installation & Start

Zum Ausführen des Programms können die Dateien im Verzeichnis "install_example" kopiert werden.
Dateien in das Verzeichnis "c:/sqleditfx" kopieren. Wenn ein anderes Verzeichnis benutzt werden soll, muss der Pfad in run.bat entsprechend angepasst werden.

Unter Datei/Server muss zuächst eine Datenbankverbindung angelegt werden. Sobald die Datenbankverbindung erstellt wurde, kann sie mit "Auswählen" ausgewählt.
Danach in "SQL:" die SQL-Abfrage erfassen und mit "Ausführen" starten.
Unter "SQL Abfragen" können SQL-Abfragen gespeichert und wieder geladen werden. Es ist möglich, Parameter im WHERE-Teil anzugeben.
IM SQL-Statements müssen hierfür "?" erfasst werden (z.B. "WHERE ARTIKEL = ?"). Die jeweiligen Bezeichnungen der Parameter sind unten in der Liste einzutragen.
Über den Schalter "Felder" im Hauptfenster kann eine Liste der Datenbank-Felder zu der jeweiligen SQL-Abfrage angezeigt werden.
Über den Schalter "Detail" im Hauptfenster kann eine Detailansicht zu dem jeweiligen Datensatz angezeigt werden. Vorher ist eine Zeile in der Tabellen-Anzeige auszuwählen.
Mit Datei/Speichern können SQL-Statements gespeichert werden.
Mit Datei/Excel ist ein Export der Daten in eine Excel-Datei möglich.

### Projekt bauen
```bash
# Projekt klonen
git clone https://github.com/lassebahn/SQLEdit.git
cd SqlEdit

# Build mit Maven
mvn clean install

Nach dem Build liegt die startfähige JAR im Verzeichnis:
target/SqlEditFX3-0.0.1-SNAPSHOT-spring-boot.jar


📦 Abhängigkeiten

Wichtige genutzte Bibliotheken:

Spring Boot
 (Apache 2.0)

JavaFX
 (GPLv2 mit Classpath Exception)

Apache POI
 (Apache 2.0)

Log4j2
 (Apache 2.0)

JSqlParser
 (Apache 2.0)

iText 2.1.7
 (LGPL/MPL)

⚠️ Hinweis zu Microsoft SQL Server JDBC

Dieses Projekt nutzt den Microsoft JDBC Driver for SQL Server (mssql-jdbc).
Dieser steht unter einer separaten Microsoft-Lizenz und ist nicht Teil der MIT-Lizenz dieses Projekts.
➡️ Lizenz: MICROSOFT SOFTWARE LICENSE TERMS

🤝 Mitwirken

Pull Requests und Issues sind willkommen.
Bitte beachte die Lizenzbedingungen dieses Projekts sowie die der eingebundenen Bibliotheken.

Dieses Projekt steht unter der MIT-Lizenz:
MIT License

Copyright (c) 2025 Lasse Schöttner

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
