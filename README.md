# AudioRoam

## Description
An exploration app for local music.

AudioRoam allows users to explore music in their proximity via markers
placed upon map.  Each marker has been dropped at that location by
another user, and contains a link to a SoundCloud song URL, as well as
information about the song such as the artist's name and the "dropper's"
comment.

## Instructions

Upon opening the app, users are greeted by a login page.  Here, they
are able to either login as an existing user, or navigate to a sign up
page.  

After logging in, a user is navigated to the map screen.  Markers for
tracks are loaded within a 200 meter radius of the user, which is intended
to encourage users to go explore their surroundings in order to find new
music.  

A user can click on a marker to display information about the song at that
location.  If the user wishes to play the song at that location, they can
click on the info window, which will load the SoundCloud URL (the player) at the
bottom of the screen.

To drop a marker, users can click the floating action button in the bottom right
hand corner.  Upon clicking, the upload page slides up from the bottom of the
screen.  Here, users will enter the artist's name, the song name, the SoundCloud
URL, as well as an optional comment to go along with that marker.  

A user can navigate to a list of their uploads or their favorited tracks.  To do so,
they can either swipe right from the left side of the map screen, or click on the
hamburger icon in the upper left hand corner.  This will open a drawer, from which
the user can select to show either their list of favorites or the tracks they have
uploaded.  The user cannot play the songs from these lists, but there is an icon
in the upper right corner of each list item, which will navigate the user directly
to the SoundCloud URL in their particular browser.  This decision was made so users
could store information about the tracks that they find while using AudioRoam, but
not so they could "save" and play the music later.  

## Credits
Developed By:
John Laws Harrison, Andrew Joung, Tyler Foster, and Adam Bourn

Developed For:
Joel Ross' Android Development course at the University of Washington iSchool.

## Our app makes use of:
Google Maps API
Google's Material Design
Google's Firebase

Background Image for Drawer retrieved from: https://www.dailydot.com/unclick/arizona-ice-tea-meme-aesthetic/
