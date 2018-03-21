# Web Proxy Server
Dette er en obligatorisk oppgave i faget INF142 fra UiB. Oppgaven var å lage en web-proxy-server som skal ta en URL, opprette kontakt med webserveren, hente noe innhold (headere), og returnere noe til klienten.

## Kort om klienten:

Klienten starter opp med valgfri IP adresse og port som kommando linje argument. 
Når klienten kjører vil den kunne ta imot noe input fra brukeren. 
Denne inputen vil bli sendt til proxy-serveren, og svaret vil bli printet ut på 
skjerment til brukeren.

## Kort om serveren:

Serveren starter opp med valgfri IP adresse og port som kommando linje argument. 
Når serveren mottar en pakke fra klienten vil den parse innholdet. 
Vi har lagt inn støtte for validering av HTTP og HTTPS, og opprettelse av sockets 
i forhold til dette. Det er også mulig å komme inn på filene til serveren, 
og få listet ut filer i mapper og enkelt filer.

Parsing av URL på serveren skjer uten bruk av hjelpe klasser. 
Her her har vi bruk regex og metoder fra String klassen til å hente ut om det er 
http/https, hostname og path, slik at vi kommer inn på riktig sted. 
Vi har også tatt hensyn til http-statuskodene 301 og 302 for redirect, og bruker da
http-headeren «Location» til å finne den riktige URL’en.

Skulle det komme en 400 eller 404, vil vi også gi en tilbakemelding til brukeren.

Vi har implementert multi tråder slik at det kan være mange klienter tilkoblet 
serveren samtidig.

Vi har lagt inn en override på shutdownhook, slik at når vi trykker `ctrl + c` 
vil programmet avslutte med den flyten vi ønsker. Dette er implementert i både 
klienten og serveren.

Filene vi henter ut fra serveren henter vi ut med lambda uttrykk.

### Ønsker du å prøve?

Vi legger ved to JAR filer som du kan teste ut applikasjonen.

For å kjøre klienten skriver du:
`java -jar DatagramCommunicator5000-1.6.3-jar-with-dependencies.jar <ip> <port>`

Og for serveren skriver du: 
`java -jar WebProxyServer-1.6.3-jar-with-dependencies.jar <ip> <port>`

Og skulle du ønske å bruke spammeren skriver du:
`java -jar Spammer5000-1.6.3-jar-with-dependencies.jar <ip> <port>`
