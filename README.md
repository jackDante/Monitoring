# Monitoring App

A baby monitor is any tool that records the activity of babies and let parents watch it remotely. 
Our project is a very simply monitor app that listens the noise and, if the threshold is crossed, notify it. 
So, in order to develop this project there’s a Client phone that is connected to the Server phone.

## Getting Started

This app works using your phone’s microphone. You will only need two Android devices.
Both devices should have Monitoring App installed. 
So, via wifi, Monitoring app can connect the Client phone to the Server phone. 
When the Server hears a noise...it sends a message to the client. 
So, the Client (that is connected to the Server) will notify the user using a notification like a new Whatsapp message.

### Installing

This is a tutorial step by step in order to show how to use monitoring app:
1. Mum’s phone and dad’s phone are both at home connected to the wifi.
1. Mum opens the Monitoring app and she selects the threshold that she wants via numberpicker selector.
1. Then, she taps on Server.
1. Dad opens the Monitoring app, he selects Client and inserts the IP and port of the Mum’s phone Server.
1. Then, he can tap on the green button named “connect “. 
   1. If he wants, he can delete all fields with the white button called “clear”. 
      Notice that this operation deletes also the information about the last connection (SharedPreferences).
1. So, Mum can leave his phone near the baby and she can go to the bedroom with dad and with the dad’s phone.
1. When the Monitoring app running on the mum’s phone hears the baby’s noise, it sends a message to the dad’s phone. 
1. So, the dad’s phone makes noise (new NotificationCompat.Builder).


## Built With

* [Android Studio](https://developer.android.com/studio/?gclid=EAIaIQobChMIkf6Qzuea5wIVlE8YCh2OMQMdEAAYASAAEgL0NfD_BwE) 

### See the pdf file for additional information.
