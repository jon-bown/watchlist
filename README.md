# Title: Watchlist

# Summary:

Watchlist provides the user to be able to create custom lists of movies/tv shows that they want to
watch. Within each list the user can mark which media they have seen via the checkmark. They can
filter each watchlist by the items they have/have not seen. Users can easily see popular and trending
media items and quickly add them to their one or many of their custom watchlists at a time. The app
provides the ability to see where movies are available to stream/rent/buy.

# Demo Instructions:

- The test account has an email set up for fake@example.com and password 123456
- If the app crashes at startup in the emulator with no output, this means that the date/time of the
    emulator needs to be updated to be in sync with the api. This will resolve by cold booting the
    emulator and running again.

# Feature List:

- Bottom navigation view that allows the user to quickly navigate between the four main fragments.
- Explore tab keeps up-to-date lists of popular, playing, trending, and upcoming items
- The Explore tab has a segmented controller that allows the user to switch between Movies and TV
    items
- Lists tab keeps the user created watch lists and a floating action button to create a new list
- Search tab contains a basic search interface that allows you to search movies or tv shows and see
    the results. (Only 20 pages of api results are loaded here to save memory)
- Profile tab contains basic user info and user preferences
- The user can select a custom language that will translate all movie info into desired language (all
    languages aren't available, default is english)
- The language setting when a user profile is created defaults to the device language setting
- The user can select a custom region that will take priority when determining available streaming
    service (all regions aren't available, default is US)
- The user selected region will also influence what shows up in all of the recycler views and search
    results
- Watchlists are sorted alphabetically
- Inside each watchlist, the user can filter by all, seen, unseen options in the top action bar.
- The user can mark items in the single watchlist view as seen or unseen
- The user can swipe left to delete watchlists
- The user can swipe left to delete items from the watchlist
- The user data and settings are stored in a firestore database
- User authentication via email and password
- When a media item is clicked, it brings up an info page that contains performance, plot, and
    release data
- The api returns results in short specified pages, scrolling through the recycler views automatically
    query the next page up to 10 pages


# API Used: themoviedb.org

# Third Party Libraries:

- Glide - Glide fetches images from the urls that are retrieved from the api. Each movie or tv show
    has a set of image urls that correspond to poster/backdrop. Glide handles the updating and
    caching very well. Although I did run into some issues with the main media item view showing old
    data becasue it is only one fragment that hosts different movies/tv shows. There was a need to
    elminate cacheing on the media detail view.
- FirebaseUI/Firestore - Handles data storage and user authentication. There were two major
    challenges with firestore. The first was deserializing the MediaItem from the storage of each user
    watch list. This is an object I made to store the minimum amount of information to identify what is
    in a users watch list and how to interact with that object like if it is of type Movie or TVShow. The
    second was concurrence when adding/removing one Movie/TVShow to one or more watchlists. If
    you make too many firestore requests at once, some will get ignored because there is a backlog of
    requests. There has to be a slight delay put in place to make sure each one completes before
    moving to the next one. The solution was only adding and then only removing and not trying to do
    both of those in one call.
- Retrofit2 - JSON Deserialization.
- Okhttp3 - GET Requests
    The API was probably the easist part of this app from the experience with the
assignments. The only challenges I came across were from the API itself which I describe in the
'Limitations' section.

# Firebase Structure:


Each document in the firestore 'users' collection is identified by the specific user uid. Each document
contains the following fields:

- Adult - Set to false for this app (this option was removed last minute because the adult option
    pertains to pornographic media only. It does not pertain to rated R or TV-MA as originally thought.
    The api is not very clear about this up front.
- Country - User country preference as an ISO-3166 code
- Language - User language preference as an ISO-639 code
- Lists - A map of keys that pertain to the names of watchlists and values that are themselves maps
    that pertain to MediaItem objects.
- Seen - a list of media 'id' codes that the user has marked as 'seen'.

# Challenges:

- Figuring out multiple pages with the horizontal recycler views
- Casting the MediaItem and WatchList objects back and forth from firestore (most technically
    challenging)
- Ensuring that firestore transactions do not overlap. This was causing a lot of problems with adding
    and removing from lists. Probably my most infuriating problem because I tried a lot of solutions
    before I landed on the one that worked how I wanted.
- Ensuring that the user and data are tied together. The document name depends on the user having
    a uid, so there can't be any data passed anywhere if this condition isn't met.
- Landing on the right structure for the app. Initial design was to have a second activity for the
    media detail view but this was causing problems when there was another media item selected
    from that activity. Using fragments for everything took some work but overall much simpler for the
    view model.
- Hiding and showing the action bar ended up being a very simple solution but I spent a long time
    on that one because the bottom nav view layout was new to me and how the context/activity
    objects are accessed.
- Placing a custom action on the action bar in only one part of the app was also a new concept to
    me. It took me a bit to figure out where that logic needed to go.
- I could not figure out how to appropriately include my API key in the app. It is hardcoded in the API
    class. I know this is very bad practice but I was not willing to spend more than it was taking me to
    follow a more formal approach.

# Key Takeaways & Limitations

- I am blown away by how many UI/UX resources there are for android. Using the 'Material Design'
    website was fascinating. I learned about a lot of cool techniques and objects that are callable in
    the XML without having to install more libraries.
- The progress circle was a great example of this. As I thought more about what would make this
    app useful to someone, I landed on making the list and everything about the list known to the user.
    Of course I didn't have time to implement all my ideas but getting the progress circle up and
    running was suprisingly easy but very satisfying in the end.
- The app originally had an 'adult' setting that would set the user preference to allow this content
    from the api. The problem with that is the api doesn't have rating information about movies or tv shows, it only marks a movie 'adult' if it contains pornography. For this reason the ability of a user to change this was removed. Ideally this would have restricted the movie or tv ratings that were rated R or TV-MA and above.
- All the same regions are not returned every time from the api when requesting available streaming
    services. The reason for the inconsistency is that the regions returned by the API aren't always the
    same. This setting is more of a preference than a hard requirement.
- All languages are not available for every movie or tv show. The default is english, but ideally there
    would be consistency throughout the api even if the specific movie hadn't been intentionally
    translated into a specific language.
- The region parameter of the api call is not consistently applied, there is really no way to tell if the
    filter was applied correctly or not for every country because if the country is not available it
    defaults to the US but also has foreign content.
- There is so much data available in the API. I really wasn't able to take full advantage of the
    possibilities due to time constraints but I was able to generate tons of ideas for the future.

# The Explore Tab:

The home page is the 'Explore' view that displays horizontal scroll views of different categories.
These categories change based on whether 'Movies' or 'TV' is selected. For 'TV' the 'Upcoming'
changes to 'Airing Today' and the 'Now Playing' changes to 'Now Airing'. The lists are also based on
the selected country of the user. Changing the country can (if not available, defaults to english and
US) influence what movies or tv shows are displayed. The list will request more pages from the api
when the user scrolls further into the list. When the 'TV' or 'Movie' options are selected all of the
scroll position reset to position zero and refresh accordingly.


# The Watchlist Tab:

The second button on the bottom nav view takes the user to the Watch Lists tab, this is where the
user can create or delete watch lists. This also displays a progress circle for each list indicating the
proportion of the list items have been marked as 'seen'. At the top of the page within each list view is
an option to filter the list by items that have been seen or not.

Using the floating action button brings up a prompt to enter the name of a new watch list. When the
user clicks 'Add' it updates the list with the new item inserted alphabetically. The list name must be
unique, if the list name already exists a snackbar will tell the user that it already exists and nothing
will change.


# The Search View

The third option on the bottom navigation view is the search fragment. This fragment allows the user
to chose whether to search movies or tv shows. The api does not contain functionality for searching
all at once. The user can type a query and scroll through the results. Once an item is selected the
keyboard disappears and the media view takes over the screen. The country setting is used to focus
results to a specific region in the search results. The bottom nav view is visible above the keyboard
and navigating away from the search view also hides the keyboard.


# The Profile View

The profile view contains information about the user and preferences. The username and email
address are shown at the top and the settings section contains the options to change country and
language. When the country or language settings are clicked it another fragment takes over the
screen that contains a scrollable list of languages and countries where only one option is allowed to

be selected. Attribution is given here to the data sources. The icons take the user to their websites.

# The Media Detail View:

The single media detail view is at the heart of the WatchList app. It contains most of the core
functionality of interacting with media. It contains the 'Add' button that allows addition to any or all of
the watchlists. The 'seen' button is also found here. The user can also see the options for viewing the
media item via streaming, buying, or renting. There are two recycler views at the bottom that contain
similar and recommended media items. When the 'add' button is clicked it brings up the 'Select
Watchlists' view that allows you to add (or remove) the item from all of your watchlists at once.


If the movie or tv show is not available for streaming the container will disappear like in the example
below.

If an item from the 'Similar' or 'Recommended' section is selected, it opens another media view on
top of the originating view. This is not actually creating a new fragment for each media item, rather it
is managing a stack of MediaItem and then updating the current view with the current info.

# Code Frequency:

I am new to this view in Github. I apparently didn't have my email configured on this machine
correctly so it wasn't counting my commits as me personally even though both contributors have my
username. But here are the number of commits, insertions, and deletions.


# Code Count:

There are only three files that I cannot claim ownership of:

AuthInit.kt (41 lines)
AppGlideModule.kt (57 lines)
SwipeToDeleteCallback.kt (19 lines)

Everything else was written by me with the exception of the recycler view divider functions that I took
from previous assignments. So in total I am responsible for about 5204 lines of code in this project.


# Code Summaries:

- adapters/
    ◦ MediaAdapter.kt - Displays a list of MediaItem objects with only the name shown, used for
       search results
    ◦ MediaCardAdapter.kt - Displays a list of MediaItem objects with the only movie poster visible
    ◦ MediaWatchListItemAdapter.kt -
    ◦ ProviderAdapter.kt - Displays a list of Providers in the single MediaItemView
    ◦ StringListAdapter.kt - String list that allows any or all items to be selected via checkbox or
       clicking on the row.
    ◦ StringListSelectionAdapter.kt - String list adapter that only allows one selection. Used for the
       language and country lists.
    ◦ WatchListAdapter.kt - Displays the names of watchlists as a list
    ◦ WatchListItemAdapter.kt - Displays MediaItem objects that are contained in a given watchlist
- api/
    ◦ Countries.kt - Static object that contains all the available country preferences for the
       streaming service region.
    ◦ Languages.kt - Static object that contains all the available language preferences for the
       movie/tv show info.
    ◦ MediaItem.kt - Holds the minimal information for a movie or tv show. This is the core object
       behind the media adapters, Firestore database, and api calls. It stores the id, type, image, and
       title.
    ◦ MediaItems.kt - Converts list of either Movie or TVShow to a list of generic media items
    ◦ MediaRepository.kt - Contains function calls that interact with the MovieDBApi
    ◦ Movie.kt - Api deserialization object for a Movie
    ◦ MovieDBApi.kt - Contains all the API calls to The Movie Database
    ◦ TVShow.kt - Api deserialization object for a TVShow
    ◦ User.kt - Object use to deserialize the document and fields for a user
    ◦ Watchlist.kt - Custom list of MediaItem
- components/
    ◦ SwipeToDeleteCallback.kt - Allows the recycler views in the watchlist fragment to delete on
       swipe
- firebaseauth/
    ◦ AuthInit.kt - Initializes the authorization process for the app and sets up the current user. If no
       current user is available, the log-in sequence is triggered.
- firestore/
    ◦ UserDBClient.kt - Client for firestore document updating and retrieveal.
- glide/
    ◦
- providers/
    ◦ Provider.kt - data class that defines a media item provider constructed from the api results
       (buy, rent, stream).
    ◦ RegionContainer.kt - data class that defines the streaming provider objects for a specific
       region from the api results.
- ui/
    ◦ dashboard/
       ‣ WatchListFragment.kt - Displays the list of watchlists that the user has created
    ◦ home/
       ‣ HomeFragment.kt - Home/explore fragment that displays the horizontal recycler views to the user
    ◦ media/
       ‣ MediaItemViewFragment.kt - Fragment that displays a single MediaItem and the corresponding info.
    ◦ profile/
       ‣ ProfileFragment.kt - Fragment that displays the profile info and users preferences
    ◦ search/
       ‣ SearchFragment.kt - Fragment that shows the results of a search query
    ◦ watchlist/
       ‣ SingleWatchListView.kt - Fragment that shows the media items contained in a single watch list.
       ‣ WatchListCheckView.kt - Fragment that displays a checklist to the user of watchlists that a given media item can be added or removed from.

- MainActivity.kt - Holds instance of MainViewModel and functions for hiding/showing actionbar and
    bottom nav view
- MainViewModel.kt - Holds all the session data when the app is running. Retrieves current user
    data, keeps track of all lists throughout the app, and keeps everything in sync with the
    UserDBClient/Firestore database.


