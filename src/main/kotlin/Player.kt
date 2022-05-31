import java.util.Scanner

class Player(var balance: Int, val name: String = "Example Player") {
    var hand = mutableListOf<Card>()
    var folded = false
    var currentBet = 0

    // Prompts each player to play their turn
    // Returns their action and the amount they add to the pot
    fun play(currentBet: Int): Pair<Actions, Int> {
        val betIncrease = currentBet - this.currentBet;
        println("\n$name, play your turn:")
        println("Balance: $balance")
        println("Your cards: ${hand.joinToString(", ") { "${it.value} of ${it.suit}" }}")
        println("Current bet: $currentBet")
        println("Amount you have put in pot: ${this.currentBet}")
        println("   Increase over previous bet: $betIncrease")
            val input = Scanner(System.`in`)
        if (betIncrease == 0) println("Enter your action (check, bet, fold):")
        else println("Enter your action (call, raise, fold):")
        val result = when (input.next().lowercase()) {
            "check" -> {
                if (betIncrease == 0) {
                    Pair(Actions.CHECK, 0)
                } else {
                    println("Checking is not an option because the current bet is not 0. Please try again:")
                    return play(betIncrease)
                }
            }
            "bet" -> {
                if (betIncrease == 0) {
                    println("Please enter how much you would like to bet:")
                    var bet = input.nextInt()
                    if (bet > balance) {
                        println("That is more money than you have, so you have gone all in. Good luck!")
                        bet = balance
                    }
                    Pair(Actions.BET, bet)
                } else {
                    println("Betting is not an option because the current bet is not 0. Use \"raise\" to increase the current bet. Please try again:")
                    return play(betIncrease)
                }
            }
            "call" -> {
                if (betIncrease == 0) {
                    println("The current bet is 0, so calling it is a check.")
                    Pair(Actions.CHECK, 0)
                } else {
                    Pair(Actions.CALL, betIncrease)
                }
            }
            "fold" -> {
                folded = true
                Pair(Actions.FOLD, 0)
            }
            "raise" -> {
                println("Please enter by how much you would like to raise the bet:")
                var bet = input.nextInt() + betIncrease
                if (bet > balance) {
                    println("That is more money than you have, so you have gone all in. Good luck!")
                    bet = balance
                }
                Pair(Actions.RAISE, bet)
            }
            else -> {
                println("Invalid choice. Please try again:")
                play(betIncrease)
            }
        }
        this.currentBet += result.second
        this.balance -= result.second
        return result;
    }
}

enum class Actions {
    CHECK, BET, FOLD, CALL, RAISE
}