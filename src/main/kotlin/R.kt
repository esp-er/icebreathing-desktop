package patriker.breathing.iceman

const val lang = "en" //TODO: actually allow different languages
object StrRes{
    //Start Screen
    val start: String
    val rounds: String
    val round: String
    val breaths: String
    val holdtimes: String
    val minus5: String
    val plus5: String

    //Prepare Screen
    val getready: String

    //Breathe Screen
    val fullyin: String
    val exhale: String
    val breatheout: String

    //Hold Screen
    val inhalein: String
    val inhale: String
    val exhalein: String

    //Canvas
    val paused: String

    //Finish Screen
    val finished: String
    val greeting: String
    val back: String
    val close: String
    val holdbreath: String

    init{
        start = "Start!"
        exhale = "Exhale"
        rounds = "Rounds"
        round = "Round"
        breaths = "Breaths"
        holdtimes = "Hold Times"
        fullyin = "Fully\n  In!"
        breatheout = "Breathe\n  Out"
        plus5 = "5+"
        minus5 = "5-"


        holdbreath = "Hold"

        getready = "Get Ready!"

        inhale = "Inhale!"
        inhalein = "Inhale In"
        exhalein = "Exhale In"
        paused = "Paused"

        finished = "Breathing Session Finished."
        greeting = "Great Job!"
        close = "Close"
        back = "Back"

    }
}
