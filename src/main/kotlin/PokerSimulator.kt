class PokerSimulator(val playerNames: List<String>, val startingAmount: Int = 0) {
    val playerCount: Int = playerNames.size
    val players = mutableListOf<Player>()
    val deck = mutableListOf<Card>()
    val discardPile = mutableListOf<Card>()
    val tableCards = mutableListOf<Card>()
    var pot = 0
    var bigBlindIndex = 1
    var smallBlindIndex = 0
    val bigBlind = 5
    val smallBlind = 2
    var currentBet = 0

    init {
        // Add players
        for (name in playerNames) players.add(Player(startingAmount, name))

        // Initialize deck
        initializeDeck()
    }

    fun playGame() {

        // Shuffle deck
        deck.shuffle()

        // Deal two cards to each player
        dealCards()

        cheatersView() // for demonstration purposes; remove for actual game

        // Add blinds to pot
        players[smallBlindIndex].addMoneyToPot(smallBlind)
        players[bigBlindIndex].addMoneyToPot(bigBlind)

        // Set current bet to big blind
        currentBet = bigBlind

        // Perform a round of betting based on the cards in players' hands
        bettingRound()

        // Reveal table cards (3 at once, then 1, then 1), performing betting rounds after each reveal
        for (i in 0 until 3) {
            revealCards()
            bettingRound()
        }

        cheatersView() // for demonstration purposes; remove for actual game

        // End game: determine winner and allocate winnings
        findWinner().distributeWinnings()

        // Reset game for next round
        resetRound()
    }


    fun initializeDeck() {
        for (suit in listOf("spades", "hearts", "diamonds", "clubs")) {
            for (value in 1..13) {
                deck.add(Card(value, suit))
            }
        }
    }

    fun dealCards() {
        for (i in bigBlindIndex until 2 * playerCount + bigBlindIndex) {
            players[i % playerCount].hand.add(deck.removeFirst())
        }
    }

    fun cheatersView() {
        println("___C_H_E_A_T_E_R_'_S___V_I_E_W___")
        println("table cards: ")
        println(tableCards)
        println("pot: $pot")
        for (player in players) {
            println("\nplayer: ${player.name}")
            println("hand: ${player.hand.joinToString(", ") { "${it.value} of ${it.suit}" }}")
            println("balance: ${player.balance}")
        }
        println("_________________________________")
    }

    fun revealCards() {
        discardPile.add(deck.removeFirst())
        for (i in 0 until when (tableCards.size) {
            0 -> 3
            3 -> 1
            4 -> 1
            else -> 0
        }) {
            tableCards.add(deck.removeFirst())
        }
        println("\nANNOUNCER: I will now reveal some cards on the table.")
        println("ANNOUNCER: The table cards are: ${tableCards.joinToString(", ")}")
    }

    fun Player.addMoneyToPot(amount: Int) {
        this.balance -= amount
        this.currentBet += amount
        pot += amount
    }

    // Present players with the opportunity to bet: go around the table at least once, then continue as long as the bets are not settled
    fun bettingRound() {
        var i = bigBlindIndex + 1
        val oneRoundIndex = bigBlindIndex + playerCount
        while (!betsSettled() || i <= oneRoundIndex) {
            if (!players[i % playerCount].folded) {
                val playResult = players[i % playerCount].play(currentBet)
                println("ANNOUNCER: ${players[i % playerCount].name} has chosen to ${playResult.first.name}, adding ${playResult.second} to the pot.")
                if (playResult.first == Actions.RAISE || playResult.first == Actions.BET) currentBet =
                    players[i % playerCount].currentBet
                pot += playResult.second
            }
            i++
        }

    }

    fun betsSettled(): Boolean {
        for (player in players) {
            if (!player.folded && player.currentBet < currentBet) return false
        }
        return true
    }

    fun Player.distributeWinnings() {
        println("${this.name} wins $pot from the pot.")
        this.balance += pot
        pot = 0
    }

    fun findWinner(): Player {
        var winner = players[0]
        for (player in players) {
            if (!player.folded && CardCollection(player.hand + tableCards) > CardCollection(winner.hand + tableCards)) winner =
                player
        }
        println("ANNOUNCER: ${winner.name} wins!")
        println("Table cards: ${tableCards.joinToString(", ")}")
        println("${winner.name}'s cards: ${winner.hand.joinToString(", ")}")
        return winner
    }

    fun resetRound() {
        for (player in players) {
            player.currentBet = 0
            player.folded = false
            discardPile.addAll(player.hand)
            player.hand.clear()
        }
        deck.addAll(discardPile)
        discardPile.clear()
        deck.addAll(tableCards)
        tableCards.clear()
        pot = 0
        currentBet = 0
        bigBlindIndex = (bigBlindIndex + 1) % playerCount
        smallBlindIndex = (smallBlindIndex + 1) % playerCount
    }

}

fun main(args: Array<String>) {
    PokerSimulator(listOf("Player 1", "Player 2", "Player 3", "Player 4", "Player 5"), 300).playGame()
}
