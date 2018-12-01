package ec.erickmedina.fussionminds.entities

data class ActivityResult (val type:ActivityResult.Type, val date: Long){
    enum class Type{
        WALK, RUN, STATIONARY, BIKE, VEHICLE
    }
}