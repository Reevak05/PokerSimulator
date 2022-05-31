import java.sql.SQLOutput

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

    fun playGame() {

        // Add players
        for (name in playerNames) players.add(Player(startingAmount, name))

        // Initialize deck
        initializeDeck()

        // Deal two cards to each player
        dealCards()

        cheatersView()

        // Add blinds to pot
        players[smallBlindIndex].addMoneyToPot(smallBlind)
        players[bigBlindIndex].addMoneyToPot(bigBlind)

        currentBet = bigBlind

        bettingRound()

        revealCards()

        bettingRound()

        revealCards()

        bettingRound()

        revealCards()

        bettingRound()


    }


    fun initializeDeck() {
        for (suit in listOf("spades", "hearts", "diamonds", "clubs")) {
            for (value in 1..13) {
                deck.add(Card(value, suit))
            }
        }
        deck.shuffle()
    }

    fun dealCards() {
        for (i in bigBlindIndex until 2 * playerCount + bigBlindIndex) {
            players[i % playerCount].hand.add(deck.removeFirst())
        }
    }

    fun cheatersView() {
        println("table cards: ")
        println(tableCards)
        println("pot: $pot")
        for (player in players) {
            println("\nplayer: ${player.name}")
            println("hand: ${player.hand.joinToString(", ") { "${it.value} of ${it.suit}" }}")
            println("balance: ${player.balance}")
        }
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
        println("ANNOUNCER: The table cards are: ${tableCards.joinToString(", ") { "${it.value} of ${it.suit}" }}")
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
                if (playResult.first == Actions.RAISE || playResult.first == Actions.BET) currentBet = players[i % playerCount].currentBet
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

    fun distributeWinnings() {

    }

    fun calculateHandValue(playerHand: MutableList<Card>, tableCards: MutableList<Card>): Double {
        val cards: List<Card> = playerHand + tableCards
        return 0.0
    }


    // hand identification
    fun containsPair(cards: List<Card>): Boolean {
        for (i in cards.indices) {
            for (j in i + 1 until cards.size) {
                if (cards[i].value == cards[j].value) return true
            }
        }
        return false
    }

    fun containsTwoPair(cards: List<Card>): Boolean {
        var pairs = 0
        for (i in cards.indices) {
            for (j in i + 1 until cards.size) {
                if (cards[i].value == cards[j].value) {
                    pairs++
                    if (pairs == 2) return true
                }
            }
        }
        return false
    }

}

fun main(args: Array<String>) {
    PokerSimulator(listOf("Player 1", "Player 2", "Player 3", "Player 4", "Player 5"), 300).playGame()
}
