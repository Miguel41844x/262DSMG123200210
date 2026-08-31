fun main() {
    val count: Int = 2
    println(count)
}

fun UnreadMessages() {
    val count: Int = 2
    println("You have $count unread messages.")
}

fun Operations() {
    val unreadCount = 5
    val readCount = 100
    println("You have ${unreadCount + readCount} total messages in your inbox.")
}

fun Cars() {
    var cartTotal = 0
    println("Total: $cartTotal")

    cartTotal = 20
    println("Total: $cartTotal")
}

fun UnreadMessages2() {
    var count = 10
    println("You have $count unread messages.")
    count = count + 1
    println("You have $count unread messages.")
    count++
    println("You have $count unread messages.")
    count--
    println("You have $count unread messages.")
}

fun DoubleType() {
    val trip1: Double = 3.20
    val trip2: Double = 4.10
    val trip3: Double = 1.72
    val totalTripLength: Double = 0.0
    println("$totalTripLength miles left to destination")
}

fun StringType() {
    val nextMeeting = "Next meeting: "
    val date = "January 1"
    val reminder = nextMeeting + date
    println(reminder)
}

fun BooleanType() {
    val notificationsEnabled: Boolean = false
    println("Are notifications enabled? " + notificationsEnabled)
}

// This is a comment.

/*
 * This is a very long comment that can
 * take up multiple lines.
 */