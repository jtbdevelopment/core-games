package com.jtbdevelopment.games.state

/**
 * Date: 11/14/14
 * Time: 12:34 PM
 */
enum GamePhase {
    Challenged('Challenge delivered.', 'Challenged'),  /*  Agreement from initial players  */
    Declined('Challenge declinedTimestamp.', 'Declined', 7),  /*  Challenged was rejected by a player */
    Quit('Game quit.', 'Quit', 7),  /*  Player Quit, similar to Declined but after game started  */
    Setup('Game setup in progress.', 'Setup'), /*  Setting word phrases  */
    Playing('Game in play!', 'Play'),
    RoundOver('Round finished.', 'Played', 7),  /*  Option to continue to a new game  */
    NextRoundStarted('Next round begun.', 'Finished', 7)

    String description
    String groupLabel
    int historyCutoffDays  //  When querying for games, how far back do we go (positive days)

    GamePhase(final String description, final String groupLabel, int historyCutoffDays = 30) {
        this.description = description
        this.groupLabel = groupLabel
        this.historyCutoffDays = historyCutoffDays
    }

}