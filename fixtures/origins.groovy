import com.force5solutions.care.aps.Origin
import static com.force5solutions.care.aps.Origin.*

fixture {
    manualOrigin(Origin) {
        name = MANUAL
    }

    timFeedOrigin(Origin) {
        name = TIM_FEED
    }

    picturePerfectFeedOrigin(Origin) {
        name = PICTURE_PERFECT_FEED
    }

}
