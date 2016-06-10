]definition(
    name: "Timed Door Lock",
    namespace: "jcholpuch",
    author: "Arnaud",
    description: "Automatically locks a specific door X minutes after unlock.",
    category: "Safety & Security",
    iconUrl: "http://www.gharexpert.com/mid/4142010105208.jpg",
    iconX2Url: "http://www.gharexpert.com/mid/4142010105208.jpg"
)

preferences{
    section("Select the door lock:") {
        input "lock1", "capability.lock", required: true
    }
//    section("Select the door contact sensor:") {
//    	input "contact", "capability.contactSensor", required: true
//    }   
    section("Automatically lock the door when closed...") {
        input "minutesLater", "number", title: "Delay (in minutes):", required: true
    }
//    section("Automatically unlock the door when open...") {
//        input "secondsLater", "number", title: "Delay (in seconds):", required: true
//    }
//    section("Notifications") {
//        input "sendPush", "bool", required: false, // RW - Fixed broken push notification handling
//              title: "Send Push Notification?" // RW - Fixed broken push notification handling
//		input "phoneNumber", "phone", title: "Enter phone number to send text notification.", required: false
//    }
    section("Send Notifications?") {
        input("recipients", "contact", title: "Send notifications to") {
            input "phone", "phone", title: "Warn with text message (optional)",
                description: "Phone Number", required: false
        }
    }
}


def installed(){
    initialize()
}

def updated(){
    unsubscribe()
    unschedule()
    initialize()
}

def initialize(){
    log.debug "Settings: ${settings}"
    subscribe(lock1, "lock", doorHandler, [filterEvents: false])
    subscribe(lock1, "unlock", doorHandler, [filterEvents: false])  
//    subscribe(contact, "contact.open", doorHandler)
//    subscribe(contact, "contact.closed", doorHandler)
}

def lockDoor(){
    log.debug "Locking the door."
    lock1.lock()
//    log.debug ( "Sending Push Notification..." )
//    if (sendPush) sendPush("${lock1} locked after ${minutesLater} minutes!") // RW - Fixed broken push notification handling
//    if ( phoneNumber != "0" ) sendSms( phoneNumber, "${lock1} locked after ${minutesLater} minutes!" )            
    log.debug "recipients configured: $recipients"

    def message = "${lock1} locked after ${minutesLater} minutes!"
    if (location.contactBookEnabled && recipients) {
        log.debug "contact book enabled!"
        sendNotificationToContacts(message, recipients)
    } else {
        log.debug "contact book not enabled"
        if (phone) {
            sendSms(phone, message)
        }
    }
}

def unlockDoor(){
    log.debug "Unlocking the door."
    lock1.unlock()
//    log.debug ( "Sending Push Notification..." ) 
//    if (sendPush) sendPush("${lock1} unlocked after ${contact} was opened for ${secondsLater} seconds!") // RW - Fixed broken push notification handling
//    if ( phoneNumber != "0" ) sendSms( phoneNumber, "${lock1} unlocked after ${contact} was opened for ${secondsLater} seconds!" )
    log.debug "recipients configured: $recipients"

    def message = "${lock1} unlocked after ${contact} was opened for ${secondsLater} seconds!"
    if (location.contactBookEnabled && recipients) {
        log.debug "contact book enabled!"
        sendNotificationToContacts(message, recipients)
    } else {
        log.debug "contact book not enabled"
        if (phone) {
            sendSms(phone, message)
        }
    }
}

def doorHandler(evt){  // JCH - Simplify door handler for door without open/close switch
    if ((evt.value == "unlocked")) { // If the door is closed and a person unlocks it then...
        def delay = (minutesLater * 60) // runIn uses seconds
        runIn( delay, lockDoor ) // ...schedule (in minutes) to lock.
    }
    else if ((evt.value == "locked")) { // If the door is closed and a person manually locks it then...
        unschedule( lockDoor ) // ...we don't need to lock it later.
    }   
}
