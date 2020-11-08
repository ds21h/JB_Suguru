# Suguru

This is an Android Suguru(Tectonic) game. It should work on all versions of Android starting from KitKat (4.4).

If you trust me feel free to download and install the .apk file.
You can also take the source as a basis to build your own game.

If you have any questions or remarks please send me a mail on ds21h@hotmail.com

History

Version 1.0.1 - 08-11-2020
-   Save on Switch Playfield was still synchronous. Now asynchronous.
-   Created exception: When an empty cell is filled and marked as erroneous en then erased the pencil entries are not erased.
-   Split Cell in ValueCell that holds the playing values and GameCell that holds some redundant information (for setup and for drawing). ValueCell is used in the Playfield, while GameCell is used in the Game.
-   Split SuguruGame in SuguruGameBase, which holds all the playing logic an SuguruGame (which inherits SuguruGameBase) that holds the multi-Playfield logic.
-   Drop the attributes SetupSel and SetupTaken from table Cell. These are redundant and now included in GameCell.

Version 1.0 - 15-09-2020
-   Introduced multiple playfields
-   Save actions to DB are asynchronous
-   Save/restore of current game in onStop/onStart instead of onPause/onResume
-   Auto pencil function can be turned off (per playfield)
-   Start game from lib can be set to only new games or all games
-   Difficulty of Libgame can be changed (then also game is reset to new)
-   Difficulty of libgame is displayed in header
-   Option "just take one" is added in start lib game
-   When auto pencil is enabled and a cellselection is changed or erased (not added!) all pencilentries are erased as they then become unreliable.
-   Minimal API level raised to 19 (Android 4.4).

Version 0.92 - 16-08-2020
-   Corrected error in DB handling with new installation.

Version 0.91 - 21-07-2020
-   Corrected error in group composition (already taken cells were also allowed).
-   Corrected error in store function. Game was not marked as Lib.
    This was caused by the fact that onActivityResult is executed before onResume.

Version 0.9 - 03-07-2020
-   First version
-   Features:
    -   Enter game up tp 9 rows/9 columns/maxvalue 9
    -   Save game in Library
    -   Random pick game from library
    -   Pencil mode
    -   Fill possible values in pencil mode
    -   Erase all pencil markings
    -   Auto-erase pencil markings on filling a cell
    -   Reset game
-   User interface in English and Dutch
