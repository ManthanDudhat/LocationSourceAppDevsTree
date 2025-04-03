# LocationSourceAppDevsTree

Definition:
1. Add/Update/Delete - Implement the functionality of add/update/delete location sources. Functionality should store all locations in persistence storage (Use local storage database).
2. Search - To add the location source to give functionality for search, Search will show suggestions based on input.
3. Sorting - Functionality of sorting locations (ascending and descending both) on the listing page of locations  
4. Preview - Could you show the location marker on the path of all the added locations and draw a path connecting each location? Make sure it is a relevant road path and not a bird-eye view? 

Flow Screen Wise
Screen 1: A list of locations with buttons for adding a source location and showing the path. The user can edit or delete the source location from the list.
Screen 2:  On Tap of add source location -  With the help of the search option, all locations will be added. 
On typing, it should show me suggestions based on my input value. (You can use Google Places API for searching, the Place API response has the coordinates for that source, which will help in sorting.)

Screen 3: Map screen - This will open when the user clicks on the show path from the listing page. 
The map screen should show markers on the map for all the location sources and draw a path connecting all the sources.
Sorting Example. (Distance-based sorting)
Input 1 - Mumbai, Ahmedabad, Boroda, Surat.
Output (Asc) -  Mumbai, Surat, Boroda, Ahmedabad.
Output (Desc) -  Ahmedabad, Baroda, Surat, Mumbai

Google Place API Enabled Key
