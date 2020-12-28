package be.vives.ti.exception;

/**
 * Enum die een gestandaardiseerde foutboodschap voor een ApplicationException voorstelt.
 */
public enum ApplicationExceptionType {

    //ongeldige eigenschappen lid
    LID_NULL("Er werd geen lid opgegeven."),
    LID_ID("Er werd geen lidid opgegeven."),
    LID_VOORNAAM_LEEG("Er werd geen voornaam opgegeven."),
    LID_NAAM_LEEG("Er werd geen naam opgegeven."),
    LID_EMAIL_LEEG("Er werd geen emailadres opgegeven. "),
    LID_RR_LEEG("Er werd geen rijksregisternummer opgegeven. "),
    LID_STARTDATUM_LEEG("Er werd geen startdatum opgegeven. "),


    //ongeldige operaties lid
    LID_UITGESCHREVEN("Het lid is uitgeschreven."),
    LID_BESTAAT_AL("Er bestaat al een lid met dit rijksregisternummer. "),
    LID_BESTAAT_NIET("Het lid werd niet gevonden. "),
    LID_STARTDATUM_TE_RECENT("De startdatum kan niet jonger zijn dan de eerste rit van het lid. "),
    LID_HEEFT_ACTIEVE_RITTEN("Het lid heeft nog ritten die niet beëindigd zijn. "),

    //ongeldige eigenschappen en operaties fiets
    FIETS_NULL("Er werd geen fiets opgegeven. "),
    FIETS_ID_WORDT_GEGENEREERD("De fiets krijgt automatisch een id en mag dus niet opgegeven worden. "),
    FIETS_BESTAAT_AL("Er bestaat al een fiets met dit registratienummer. "),
    FIETS_BESTAAT_NIET("De fiets werd niet gevonden. "),
    FIETS_NIET_CORRECTE_STATUS("De fiets heeft niet de correcte status. "),






    ;

    private final String message;

    ApplicationExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}