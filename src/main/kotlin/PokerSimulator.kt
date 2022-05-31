import java.util.*

class PokerSimulator(playerNames: List<String>, startingAmount: Int = 0) {
    private val playerCount: Int = playerNames.size
    private val players = mutableListOf<Player>()
    private val deck = mutableListOf<Card>()
    private val discardPile = mutableListOf<Card>()
    private val tableCards = mutableListOf<Card>()
    private var pot = 0
    private var bigBlindIndex = 1
    private var smallBlindIndex = 0
    private val bigBlind = 5
    private val smallBlind = 2
    private var currentBet = 0

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

        // Ask player to play again
        val input = Scanner(System.`in`)
        println("Would you like to continue playing?")
        if (input.nextLine().lowercase() == "yes") playGame()
        else println("Thank you for playing! Goodbye.")
    }


    private fun initializeDeck() {
        for (suit in listOf("spades", "hearts", "diamonds", "clubs")) {
            for (value in 1..13) {
                deck.add(Card(value, suit))
            }
        }
    }

    private fun dealCards() {
        for (i in bigBlindIndex until 2 * playerCount + bigBlindIndex) {
            players[i % playerCount].hand.add(deck.removeFirst())
        }
    }

    private fun
            cheatersView() {
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

    private fun revealCards() {
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

    private fun Player.addMoneyToPot(amount: Int) {
        this.balance -= amount
        this.currentBet += amount
        pot += amount
    }

    // Present players with the opportunity to bet: go around the table at least once, then continue as long as the bets are not settled
    private fun bettingRound() {
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

    private fun betsSettled(): Boolean {
        for (player in players) {
            if (!player.folded && player.currentBet < currentBet) return false
        }
        return true
    }

    private fun Player.distributeWinnings() {
        println("${this.name} wins $pot from the pot.")
        this.balance += pot
        pot = 0
    }

    private fun findWinner(): Player {
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

    private fun resetRound() {
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
