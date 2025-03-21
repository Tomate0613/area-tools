# Area Tools

Area Tools provides map creators with tools to manage gameplay within specific areas

Areas are managed using [Area Lib](https://modrinth.com/mod/area_lib)

## Features

- Item area restriction using item components
- Area Respawn Points – Players who die inside an area with a respawn point will respawn there
- Control pvp usage with `area_tools:pvp_disabled` and `area_tools:pvp_enabled`
- Automatically enable figura panic mode for players inside `area_tools:figura_panic`, restoring their
  previous state upon exit
- `area_tools:area_check` loot condition
- Run commands when entering or exiting an area

### Commands
`/area_tools` is used to set spawnpoint and enter/exit events for areas

### Items
These are just shortcuts for commands

- Area Creator – Used to define and manage custom areas. 
- Area Spawnpoint Setter – Sets a respawn point within an area.

### Item Components

- `area_tools:can_use_in_area` restricts item usage to a specified area
- `area_tools:dissolve` Deletes item once leaving area

