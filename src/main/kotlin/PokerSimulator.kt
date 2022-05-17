class PokerSimulator(val playerCount: Int = 0, val startingAmount: Int = 0) {
    val players = mutableListOf<Player>()
    val deck = mutableListOf<Card>()
    val discardPile = mutableListOf<Card>()
    val tableCards = mutableListOf<Card>()
    var bigBlindIndex = 0
    var smallBlindIndex = 0

    fun playGame() {

        for (i in 0 until playerCount) players.add(Player(startingAmount))

        initializeDeck()

        dealCards()

        cheatersView()

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
        for (player in players) {
            println("\nplayer:")
            println("hand: ${player.hand.joinToString(", ") { "${it.value} of ${it.suit}" }}")
            println("balance: ${player.balance}")
        }
    }


}

fun main(args: Array<String>) {
    PokerSimulator(5, 300).playGame()
}