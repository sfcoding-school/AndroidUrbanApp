

# ANDROID APPLICATION
This is an example of **Android application**, this application can be used as **prototype** for a Club, it keeps updated the user about future events, artist videos, prices and bus time schedules, using it can allow the final user to access a special list discount.  


## Conceptual Working Demo

The application uses the **Facebook Social Login** to identify the user, to get the events and user informations it interacts with a server developed in [Python2.7](https://www.python.org/) using the [DJango](https://www.djangoproject.com/) framework. ([Click Here](https://github.com/sn1p3r46/urbanserver) The server Repo)



#### Facebook Login Activity:

<p align="center">
  <img src="images/StartPage.png" width="300">
</p>

#### User Profile Logged-In

**Two different UIs:**

<p align="center">
  <img src="images/loggedin1.png" width="300">
  <img src="images/loggedin.png" width="300">
</p>

#### Event Activity:

Both the cover image and background should change for every event. The background one is the same of the cover but blurred.

<p align="center">
  <img src="images/event0.png" width="300">
  <img src="images/event1.png" width="300">
  <img src="images/event2.png" width="300">
</p>


#### Notification:

If the user has expressed its interest on a specific event the application will remind the user the day of the event with a notification.

<p align="center">
  <img src="images/notification.png" width="500">
</p>

#### Notes:

The application can not be launched as it needs some code review because is based on some Facebook API that now are deprecated. 
