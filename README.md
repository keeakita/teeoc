# Tee O.C.

This program allows you to provide a pack of visual/sound assets to be swapped into a copy of Tee K.O.. It provides a way to play with your own character images and sounds in the main display (this does not change anything shown on player's devices).

## Current status

You can right now (manually) create a resource pack that can swap images into the game. I've only tested it with the character select icons.

Future plans include testing packs that include images for every part of the game, packs that have sound and animation resources, and possibly a GUI for creating resource packs.

## Resource Pack Format

A resource pack is just a collection of assets in a Zip file. Standard zip programs should work just fine -- this uses the Java standard library Zip reader. Assets are anything supported by FFDec. For images, this is at least PNG and JPEG (possibly others?). For sounds, I have no idea. I haven't tried it yet.

The directory structure encodes which assets should be swapped where. Consult `src/main/resources/teeko_ids.yaml` for the names of everything.

As an example, consider a Zip file with the following entries that replaces character select icons for 5 characters:

```
character_select/
character_select/visual_assets/
character_select/visual_assets/character_select_icons/
character_select/visual_assets/character_select_icons/cat.png
character_select/visual_assets/character_select_icons/dog.png
character_select/visual_assets/character_select_icons/red_demon.png
character_select/visual_assets/character_select_icons/umbrella.png
character_select/visual_assets/character_select_icons/snake.png
```

## License Disclaimer

I am providing code in this repository to you under an open source license. Because this is my personal repository, the license you receive to my code is from me and not from my employer (Facebook).

## Why GPL?

This program for the most part is a wrapper around [the JPEXS Free Flash Decompiler](https://www.free-decompiler.com/flash/0), which is provided under the GPL. It's not really a library, I had to rip into the source code and copy portions of it to make this work since there's no real documentation. Since I'm directly taking GPL source, this project has to be GPL.
