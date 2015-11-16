core-games
==========

Common Utilities for Games

games
=====
- Defines a base player and variants, as well as abstract spring-data repository
- Defines base exceptions
- Defines base interfaces for finding socially linked players
- Defines base interface for interacting with Session

games-mongo
===========
- Concrete spring-data-mongo implementations of player repository
- Serialization solutions for ObjectId
- Concrete implementations of player and manual player friend finder

games-dictionaries
==================
- Some dictionaries and utilities for games requiring word validation

games-web
=============
- Presumes use of spring-security, spring-social, jersey and jackson
- Provides utilities for interacting with those services and maps back to players.
- Also provides several core security and social rest api's via jersey for web
- Registers a generic jackson ObjectMapper context resolver

games-web-std-multiplayer
=========================
- services more common for multiplayer games

games-std-tracker
=================
Hooks for monitoring and limiting player games per day.

game-events
===========
Hub for pushing game/player events around different services

games-hazelcast
==============
Using hazelcast to push game/player updates across a cluster of servers to allow publication within each server.

Primary example would be for websockets where players are on different servers and notification needs to go to each of them.

games-websocket
===============
Standard implementation for push notifications to clients actively listening.  Only sensible for multi-player.
 
games-push-notifications
========================
If websocket fails, push to google/ios phones where applicable.

games-dev-utilities
===================
Not for production - some utilities for 
 - a common jetty service for testing
 - creating standard dummy users