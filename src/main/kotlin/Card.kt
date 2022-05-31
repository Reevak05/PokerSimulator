class Card(val value: Int, val suit: String) {
    override fun toString(): String {
        return "${
            when (value) {
                1 -> "Ace"
                11 -> "Jack"
                12 -> "Queen"
                13 -> "King"
                else -> value
            }
        } of $suit"
    }
}

class CardCollection(val cards: List<Card>) : Comparable<CardCollection> {

    /**
     * @param ignoreCardValue optional card value to ignore when checking for matching cards; used when checking for a full house
     * @return Pair<Int, Int>: first item is number of matching cards; second item is value of the matching cards
     */
    fun maxNumberOfMatchingCards(ignoreCardValue: Int = -1): Pair<Int, Int> {
        //TODO: make this return the highest value if there are multiple matches of the same number of cards
        val sortedCards = cards.sortedBy { card -> card.value }
        var maxMatchingCards = 1
        var maxMatchingCardValue = -1
        var matchingCards = 1
        for (card in 0 until sortedCards.size - 1) {
            if (sortedCards[card].value != ignoreCardValue && sortedCards[card].value == sortedCards[card + 1].value) matchingCards++
            else matchingCards = 1
            if (matchingCards > maxMatchingCards) {
                maxMatchingCards = matchingCards
                maxMatchingCardValue = sortedCards[card].value
            }
        }
        return Pair(maxMatchingCards, maxMatchingCardValue)
    }

    /**
     * @return Int value of highest card in straight or -1 if no straight
     */
    fun containsStraight(): Int {
        val sortedCards = cards.sortedBy { card -> card.value }
        var orderedCards = 1
        for (card in 0 until sortedCards.size - 1) {
            if (sortedCards[card].value + 1 == sortedCards[card + 1].value || (sortedCards[card].value == 13 && sortedCards[card + 1].value == 1)) orderedCards++ // if the next card is one higher (with a special case for a king and an ace)
            else orderedCards = 1
            if (orderedCards == 5) return sortedCards[card + 1].value
        }
        return -1
    }

    /**
     * @return Int value of highest card in flush or -1 if no flush
     */
    fun containsFlush(): Int {
        val groupedCards = cards.groupBy { card -> card.suit }
        for (group in groupedCards) if (group.value.size >= 5) return if (group.value.map { card -> card.value }
                .contains(1)) 1 else group.value.maxOf { card -> card.value }
        return -1
    }

    /**
     * @return Pair<Int, Int>: first item is value of three of a kind; second item is value of pair
     * returns -1, -1 if no full house
     */
    fun containsFullHouse(): Pair<Int, Int> {
        val matchingCardsResults1 = maxNumberOfMatchingCards()
        val matchingCardsResults2 = maxNumberOfMatchingCards(matchingCardsResults1.second)
        return if (matchingCardsResults1.first >= 3 && matchingCardsResults2.first >= 2) Pair(
            matchingCardsResults1.second, matchingCardsResults2.second
        )
        else Pair(-1, -1)
    }

    /**
     * @return Int value of highest card in straight flush or -1 if no straight flush
     */
    fun containsStraightFlush(): Int {
        val flushMaxValue = containsFlush()
        val straightMaxValue = containsStraight()
        return if (flushMaxValue != -1 && flushMaxValue == straightMaxValue) flushMaxValue
        else -1
        //TODO: accommodate either the straight or the flush being longer than 5 cards, meaning the max values of the straight and the flush are not the same but the straight flush still exists
    }

    /**
     * @param other cards to compare to this
     * @return 1 if this is greater than other, 0 if this and other are equal, and -1 if other is greater than this
     */
    override fun compareTo(other: CardCollection): Int {
        val handResults = mutableListOf<Pair<Int, Int>>()
        /*
        System of assigning a value to each hand:
        first value:
            8: straight flush
            7: four of a kind
            6: full house
            5: flush
            4: straight
            3: three of a kind
            2: two pair
            1: one pair
            0: high card
        second value:
            highest card in hand
         */
        for (hand in listOf(this, other)) {
            var maxVal = Pair(0, 0)

            // Straight flush
            var handValue = hand.containsStraightFlush()
            if (handValue != -1 && 8 > maxVal.first) maxVal = Pair(8, handValue)

            // Cards of the same value
            var handValuePair = hand.maxNumberOfMatchingCards()
            if (maxVal.first < if (handValuePair.first == 4) 7 else handValuePair.first) maxVal =
                Pair(if (handValuePair.first == 4) 7 else handValuePair.first, handValuePair.second)

            // Full house
            handValuePair = hand.containsFullHouse()
            if (handValuePair.first != -1 && 6 > maxVal.first) maxVal = Pair(6, handValuePair.second)

            // Flush
            handValue = hand.containsFlush()
            if (handValue != -1 && 5 > maxVal.first) maxVal = Pair(5, handValue)

            // Straight
            handValue = hand.containsStraight()
            if (handValue != -1 && 4 > maxVal.first) maxVal = Pair(4, handValue)

            handResults.add(maxVal)
        }

        // Compare the two hands and return accordingly
        return if (handResults[0].first > handResults[1].first) 1
        else if (handResults[0].first < handResults[1].first) -1
        else {
            if (handResults[0].second > handResults[1].second) 1
            else if (handResults[0].second < handResults[1].second) -1
            else 0
        }
    }
}