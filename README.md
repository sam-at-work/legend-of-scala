## ![Logo](/tools/art/splash.png) The Legend of Scala

_A (sort of) educational game for learning Scala._

## Overview

_Legend of Scala_ is a mildly educational video game for learning Scala. The
code is mildly terrible (it's my first time using Scala so please be gentle).

### Running the Server

Play Framework needs to be installed.

1. Run `play` in the server directory.

2. Open the `console` to run `:load scripts/generate_features.scala`.

3. `run`

### Running the Client

SBT is bundled with the repo.

You must be using OSX or Linux!!

1. git clone this repo `git clone https://github.com/Dahaden/losclient.git`

2. Set the environment variable `LOS_HOST` to the server's address.

3. Run `./start.sh` in the client directory.

4. Call `var me = login(charName, token)`

5. Follow tutorial http://sesa.org.nz/los

## Tutorial

There isn't really one, sorry `:(` I might get around to writing one if this
idea is actually good.

## Attribution

Logo by Henry de Chabert

Monster faces from http://charas-project.net/

Item icons adapted from http://www.planetminecraft.com/texture_pack/-pixbits-texture-pack-frenden-inspired-and-approved-texture-pack/

Dragon from http://opengameart.org/content/dragon-1
