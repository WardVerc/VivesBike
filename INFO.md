## Gegeven database
De gegeven database wordt gebruikt.

## Extra functionaliteiten
- Voorlopig geen.

## Tekortkomingen/fouten
- Dit project maakt gebruik van de gegeven database, je hoeft dus enkel testdata toe te voegen. Er is een export gemaakt van de database met mijn testdata (zie 'vivesbike_export.sql' in /databank).
- Het project runt als je de VIVESbike class runt. Niet alle testen slagen dus je krijgt geen build success na een clean install. Sommige testen van RitService moeten nog aangepast worden.
- Ik heb het gevoel dat Rijksregisternummer bij LidDAO voor problemen kan zorgen.
Het lijkt verkeerd om telkens een nieuw Rijksregisternummer aan te maken en deze telkens om te zetten van en naar String. Het lijkt omslachtig, heb een vermoeden dat er een betere oplossing bestaat ipv wat ik gemaakt heb.
- ToevoegenRit() stel ik ook de prijs en einddatum in, ik denk dat dit niet nodig is.
- In mijn testen van RitService mock ik veel zaken, ik denk dat het beter is om zo weinig mogelijk te mocken.
- Ontbreken van tests voor LidService en FietsService.
- Tijdens het maken van LedenBeheercontroller, merkte ik dat ik geen lidservice kon maken zonder ritservice (omdat ik in de methodes van lidservice wou checken of een lid een actieve rit (= ritservice) heeft of niet).
En een ritService kan ik niet maken zonder de lidservice. Ik besefte dat ik de lidservice onafhankelijk van de ritservice moest maken en die checks op actieve ritten een niveau hoger moest zetten. 
Hierdoor is lidservice beter geëncapsuleerd.
Dit heeft als gevolg dat ik andere klassen moest aanpassen omdat je geen ritservice meer moest aanmaken als ik een lidservice aanmaakte.
Hierdoor lopen 10 testen van RitService mis. Deze moet ik nog aanpassen.
- Je kan namen zoals "." opslaan, er is geen check op symbolen etc. Hetzelfde voor email, je kan eender wat voor tekst (cijfers, symbolen) opslaan als email.
Er zouden nog checks moeten zijn die dit niet toelaten.

