# FightingGame
Its a game where you fight - The title says it all!
## AI
The AI works from a base AI class that performs a specific AIAction, the actions must have a type and dependant upon the action it can have a Fighter as a target. The "AI" is in reality just a basic state machine, as we all know deep learning nueral networks are hard to implement and have a 14% chance to take over the world. There currently are only 5 action types (derived from `EAIActionType`) those, along with thier descriptions (derived from abstract class `AIFighterBase`) are listed below: 

- Search
- Chase (Fighter): Chases, and shoots at a target happens only when the target is `chaseDist` away
- Dodge (Fighter): Dodges a fighter, happens only by calling the `startDodge(Fighter f)` method, this also has a random direction of which it will happen in
- Shoot (Fighter): Shoots at a fighter, happens only by calling the `attack(Fighter f)` method
- Do nothing (Used instead of null, because why not?)

## Libraries
This uses the processing library (https://processing.org/) for graphics, however it will later be converted to using JOGL (http://jogamp.org/jogl/www/)
