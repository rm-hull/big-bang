# Pac Man

Notes for on-going development of ClojureScript Pac Man using 
[big-bang](https://github.com/rm-hull/big-bang) as a proof-of-concept.

## Building and running

The main build script in the project directory will automatically
transpile the ClojureScript code into JavaScript - presently this
will not be optimized - until big-bang evolves into a stable library
at least.

On building, the pacman.html file in this directory can be used to
test from the target directory.

Open http://rm-hull.github.io/big-bang/pacman.html for the latest published
version.

## Development Notes

### Sprites

[sam_gfx.png](https://github.com/rm-hull/big-bang/blob/master/examples/pacman/data/sam_gfx.png)
is a 384x240 sprite sheet comprising of 12x12 and 24x24 sprites. It was 
originally created by Simon Owen (derived from http://simonowen.com/sam/articles/pacemu/)
but has been modified to remove some duplication, as well as having the borders trimmed. 
Also, see an enlarged version with gridlines:

![gridlines](https://raw.github.com/rm-hull/big-bang/master/examples/pacman/gridlines.png)

### Maps

Uses box-drawing ANSI characters (see [wikipedia](https://en.wikipedia.org/wiki/Box-drawing_characters))
for the map borders, a dot for the normal pills and a capital O for the 
energy pills.

* [Level 1](https://raw.github.com/rm-hull/big-bang/master/examples/pacman/data/1.txt)

## References

* https://en.wikipedia.org/wiki/Pixel_art_scaling_algorithms#Pixel_art_scaling_algorithms
* https://code.google.com/p/hqx/wiki/ReadMe
* http://simonowen.com/sam/articles/pacemu/
* http://www.lugnet.com/admin/plan/map/pacman.html
