# FightingGame
Its a game where you fight - The title says it all!
## AI
The AI works from a base AI class that performs a specific AIAction, the actions must have a type and dependant upon the action it can have a Fighter as a target. There currently are 4 actions

- Search
- Chase (Fighter): Chases, and shoots at a target happens only when the target is `chaseDist` away
- Dodge (Fighter): Dodges a fighter, happens only by calling the `startDodge(Fighter f)` method, this also has a random direction of which it will happen in
- Shoot (Fighter): Shoots at a fighter, happens only by calling the `attack(Fighter f)` method
- Do nothing (Used instead of null, because why not?)
