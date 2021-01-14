# Imperialist competitive algorithm visualistion

## What is it?

Imperialist competitive algorithm is [population based heuristic](https://en.wikipedia.org/wiki/Evolutionary_computation), which utilises some [swarm intelligence](https://en.wikipedia.org/wiki/Swarm_intelligence) like mechanisms and competition similar to genetic algorithm. 

[wiki link](https://en.wikipedia.org/wiki/Imperialist_competitive_algorithm)

## What am I looking at?

My implementation looks for minimum of given function. There are few predefined functions (Rastrigin, Ackley, Sphere, Rosenbrock, Himmelblau).

Big dots are metropolis(capitals/centers of empires), smaller ones are theirs colonies. Colonies are getting closer and closer to theirs metropolies. If they will find better solution in the process they become the new metropolis. Whole empires have one color.

Every iteration weakest empire loses one if its colonies, to other empire(the stronger empire the more likely it is to get a colony).

## Cryptic parameters

![pic should be here :()](https://raw.githubusercontent.com/tomsiemek/imperialistic-competitive-algorithm-visualisation/master/pics/beta-gamma.png)

**Alpha** - [0, 1] - coefficient in calculating total empire power - how important are colonies in evaluation value of empire

**Beta** - [1, ∞] - multiplier of maximum distance colony can "travel" in one iteration - how "explorative"of the space "behind" metropolis are the colonies

**Gamma** - [0, π] - maximum angle of deviation from line connecting centers - how "explorative" are colonies in their travel to the metropolis
