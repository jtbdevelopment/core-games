core-games
==========

Common Utilities for Games

games
=====
Defines a base player and variants, as well as abstract spring-data repository
Defines base exceptions
Defines base interfaces for finding socially linked players
Defines base interface for interacting with Sesison

games-mongo
===========
Concrete spring-data-mongo implementations of player repository and serialization solutions for ObjectId
Concrete implementations of player and manual player friend finder

games-dictionaries
==================
Some dictionaries and utilities for games requiring word validation

game-core-web
=============
Presume use of spring-security and spring-social and provides utilities for interacting with those services and maps back to players.
Also provides several core security and social rest api's via jersey for web