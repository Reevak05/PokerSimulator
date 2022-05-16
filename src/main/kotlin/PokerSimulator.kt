class PokerSimulator {
    val players = mutableListOf<Player>()
    val deck = mutableListOf<Card>()
    val discardPile = mutableListOf<Card>()
    val tableCards = mutableListOf<Card>()
    var playerCount = 0
    var startingAmount = 0

    fun playGame(playerCount: Int, startingAmount: Int) {
        this.playerCount = playerCount
        this.startingAmount = startingAmount

    }

    fun initializeDeck() {
        for (suit in listOf("spades", "hearts", "diamonds", "clubs")) {
            for (value in 1..13) {
                deck.add(Card(value, suit))
            }
        }
        deck.shuffle()
    }



}

fun main(args: Array<String>) {
    PokerSimulator().playGame(5, 300)
}