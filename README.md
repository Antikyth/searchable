# Searchable
_Makes Minecraft more searchable!_

Improves the search functionality of the singleplayer world selection screen and also brings it to:
- the multiplayer server selection screen
- the languages screen
- the key binds screen
- the game rules editing screen
- the resource pack selection screen
- the data pack selection screen

Please make any suggestions or bug reports in the Issues tab :)

## Why does this exist?
I created this mod initially just to add search to the Languages screen, as I am learning Dutch and I wanted to regularly switch between Dutch and English in the game, but having over 100 languages that all have the same text formatting and no way to filter them was very frustrating.  After realising that the world selection screen has its own search box, I figured the best way to go about this was to add that same search to the Languages screen, and then to other GUIs that seemed a bit neglected with their own lack of search.

![A screenshot of the Languages screen, showing the results of a RegEx search query "new+ " with the matching parts of the results underlined.](https://cdn-raw.modrinth.com/data/48eQJs3v/images/6c973d4773d84411d75845577a202348ac716649.png)

## What changes does this make to the singleplayer world selection screen?
Searchable adds text highlighting, fixes a bug where the "Play Selected World" and other buttons in the GUI wouldn't become greyed out when the selected world is hidden due to a search query change, and adds more options turned off by default to:
- use RegEx expressions for searching
- match world details as well as world names

Searchable also adds a button to open the config next to the search boxes it modifies or adds.

![A screenshot of the singleplayer world selection screen showing text highlighting and the added config button.](https://cdn-raw.modrinth.com/data/48eQJs3v/images/5072cc3c61364e8b437f93f9fba100d3e6865e84.png)

## What if I don't want certain changes that the mod makes?
That's totally fine!  Pretty much everything in Searchable can be toggled on and off in the config, from highlighting to which screens have search added to whether the 'open config' buttons are added.

The config can be changed with either:
- the in-game GUI, accessible through the 'open config' buttons or Mod Menu if it is installed; or
- the config TOML file found in `.minecraft/config/searchable/searchable.toml`.

![A screenshot of Searchable's in-game config GUI, showing the tooltip for the "Highlight Matches" option with its technical name, description, and default value.](https://cdn-raw.modrinth.com/data/48eQJs3v/images/0f4634fa026ffdba326fbea17368085540903e36.png)
