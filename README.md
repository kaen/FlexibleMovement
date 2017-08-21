# FlexibleMovement

The movement implementation for `FlexiblePathfinding`.

## About

`FlexibleMovement` implements nodes and plugins for various movement types. It's meant to be used with 
`FlexiblePathfinding`, and both modules are developed in tandem. Every `FlexiblePathfinding` plugin has a 
`FlexibleMovement` plugin equivalent.

`FlexibleMovement` provides the following:

 - Movement plugins matching the path finding plugins for `FlexiblePathfinding`
 - A plugin registry for loading and registering additional plugins
 - A movement component for configuring movement parameters and tracking the path finding and movement state
 - An `UnstickingSystem` for dislodging entities within un penetrable blocks
 - Behavior nodes and trees for obtaining and following a path from `FlexiblePathfinding`
 - A fairly robust test suite that ensures you can tinker without breaking things