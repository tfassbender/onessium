# onnessium
A turn-based strategy game, created with libGdx

## Development
Onessium is planed to be a strategy game that is turn based to focus on the strategy instead of which player can create his build-chains faster.  
Because of the lack of textures, the game will only be playable in a tactical overview (only symbols for units, but no textures).

## Build Notes

To build the client and server use the following gradle commands in the top level project:
```
gradle clean dist distServer --refresh-dependencies
```
The `--refresh-dependencies` option is needed because the project depends on a github project that is referenced using jitpack.io. Therefore the dependencies must be manually refreshed sometimes, because there is no release, but the target is the master branch.

The compiled sources are to be found in `desktop/build/libs`.

### Milestones

#### Chat Server :heavy_check_mark:
A simple chat server to test menus, layouts and client-server communication in libGdx.

#### Main Menu
A main menu with limited functions.

#### First Playable Match
A simple match with pre-defined units on an empty map.

#### More units
Implement more unit types and add them to the simple match.

#### Maps
Add maps with properties and textures.

#### Production Buildings
Add production buildings to the simple game, so that new units can be created (no resources needed yet).

#### Production Units and Resources
Add production units and new buildings, to collect resources and to create new buildings and units. Resources are needed to create units and buildings.

#### Goals
Add goals that can be defined when starting the game.

#### Diplomacy Options
Add diplomacy options, so the players can alliances.

## Story (WIP)
The year is 2242. Players fight for the dominance on a distant planet.  
On this planet, a mineral can be found that is superconductive at a temperature of 293K (20°C). This mineral is an important part of most modern robot- and weapon-systems.  
This mineral is called **Onessium** (after Heike Kamerlingh Onnes - a Dutch physicist who discovered the phenomenon of supraconductivity in 1911).