# SmartThings
SmartThings customer apps and device types

<b>Energy Meter Routine:</b>

Trigger a lighting scene/routine based on A/V equipment power consumption using Aeon Labs Smart Power switch or similar device.  I use this to automate the lighting when my projector and A/V equipment a turned on or off.
<br>1.  Check or change reporting interval on default ST meter device type.  Default reporting was 5 minutes, I used 5 second updates.
<br>2.  Install EnergyMeterRoutine SmartApp, specify power meter device, power threshold, and Routines to trigger.
<br>3.  SmartApp will trigger routine one time when power crosses above threshold and below threshold
<br>
<br>
<b>Timed Door Lock:</b>

Dumbed down version of the ST Enhanced Door Auto Lock App.  This app locks door after X minutes.  A door open/close sensor is not required.
