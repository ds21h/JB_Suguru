# Suguru

This is an Android Suguru(Tectonic) game. It should work on all versions of Android starting from Jelly Bean (4.2).

If you trust me feel free to download and install the .apk file.
You can also take the source as a basis to build your own game.

If you have any questions or remarks please send me a mail on ds21h@hotmail.com

History

Version 1.0 - 14-09-2020
    -   Introduced multiple playfields
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
