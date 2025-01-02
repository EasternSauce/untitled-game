## Abilities

Each ability can have zero or more ability components. Abilities are created by queueing an object on abilitiesToCreate queue. They start in Channelling state, with the attack animation playing. Once channelling ends, the state changes to Active and the ability is executed, which for most abilities is the point when first ability components are created. Once the ability concludes, its state changes to Finished. If the ability is in Finished state there should not anymore exist any of its components.

## Ability components

Ability components represent a physical manifestations of abilities. Each ability can have multiple components. All components don't need to be created once ability becomes active, they can be delayed until certain condition is met, eg. a chain of components, where at the end of its path the component is converted into other component.

## Creatures

Creature is an actor that can walk around in the game world, actively interacting with it. Every player is a creature, controlled by events being send from player client. Every enemy is a creature, controlled by enemy movement/attack logic. Players and enemies are created by scheduling them in the specific queues.

## Client-server communication

There are multiple was to send events related to client-server communication.

### Broadcast events

Broadcast event happens when a client needs to send immediate information to the server. Once the server receives this information, it transmits it to every other client, and then each client processes such events as local events. Main use of broadcast events is for player actions to be immediately reflected in each client's game state.

### Local events

Local event is an event that is only executed on current instance (either client or server) at the end of the game loop.

## Game state

Entire state of all game areas is saved and updated in the game state. Server and clients each hold their own separate game state updated on each pass of the game loop. Game state will naturally desynchronize between client and server, so to prevent it, the server periodically sends its version of game state to all clients and forces them to update it.

### Game state events

Game state events are the contents of broadcast or local events. They represent a small change to be performed on the game state.

## Physics

All active creatures and ability components should have their own representation in the physics worlds. Physics are used to resolve collisions between physics bodies, as well as physics bodies and terrain. Movement of creatures and abilities is also performed on physics engine by setting their linear velocity. All physics bodies are managed independently of game state, which makes it necessary that their positions are synchronized once there is too much discrepancy. Physics collisions result in sending a local event in the instance where collision occurs.

### Physics world

Each world area is represented by their own physics world. Bodies of one physics world cannot interact with bodies of another world. To move a body between worlds it's body needs to be deleted in one world and created in another.

## View

Every renderable object is also managed independently of game state. Every game loop pass a list of creatures and abilities is checked to handle deletions/additions of their renderable objects. Game state timers are checked to choose the current animation frames to render.

## Enemy spawn points

Enemies have certain points around which they spawn. Each spawn has certain number of each type of enemy to be spawned. Enemies respawn on death at the spawn point.

## Timers

Timers are held as part of game state, incremented by a delta in each game loop. A timer can be queried for current time and reset. Adding a new timer requires that it is also added to the update function.