package aaaimx.gen2021.reactiveGhosts;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public final class Ghosts extends GhostController{
	int close = 3;
	int pillClose = 10;
	Random rnd = new Random();
	EnumMap<GHOST,MOVE> moves=new EnumMap<GHOST,MOVE>(GHOST.class);
	
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		for (GHOST ghost : GHOST.values()) {
			if(game.doesGhostRequireAction(ghost)) {
				MOVE[] possibilitiesMoves= game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost),game.getGhostLastMoveMade(ghost));
				if (game.getGhostEdibleTime(ghost)>0||closeToPower(game)) {
					// meth for runway
					moves.put(ghost, getRunAwayeMove(game, ghost, possibilitiesMoves));
					//moves.put(ghost, getPersureMove(game, ghost, possibilitiesMoves));
				}else {
					// meth for persure
					moves.put(ghost, getPersureMove(game, ghost, possibilitiesMoves));
					//moves.put(ghost, getRunAwayeMove(game, ghost, possibilitiesMoves));
				}
			}
		}
		
		return moves;
	}

	private boolean closeToPower(Game game) {
		int [] powerPills = game.getPowerPillIndices();
		for(int i : powerPills) {
			if (game.isPowerPillStillAvailable(i) && (game.getShortestPathDistance(i, game.getPacmanCurrentNodeIndex())<pillClose)){
				return true;
			}
		}
		return false;
		
	}
	private MOVE getRunAwayeMove(Game game, GHOST ghost, MOVE[] possibilitiesMoves) {
		Map <MOVE, Integer> allMovesValues = new HashMap <MOVE,Integer>(possibilitiesMoves.length);
		int ghLocation = game.getGhostCurrentNodeIndex(ghost);
		int pcLocation = game.getPacmanCurrentNodeIndex();
		for (MOVE move : possibilitiesMoves) {
			int neighbour=game.getNeighbour(ghLocation,move);
			int distanceValue = game.getShortestPathDistance(pcLocation, neighbour);
			for (GHOST otherGhost: GHOST.values()) {				
				int oGhLocation = game.getGhostCurrentNodeIndex(otherGhost);
				int distancePcGh = game.getShortestPathDistance(pcLocation, oGhLocation);
				if (ghost != otherGhost && distancePcGh>0) {					
					int[] path= game.getShortestPath(pcLocation, oGhLocation); 
					path= game.getShortestPath(pcLocation, oGhLocation);
					for (int node : path) {
						if (node == neighbour && game.isGhostEdible(otherGhost)) {
							distanceValue +=50;
						}else if (node == neighbour){
							distanceValue +=90;
						}
					}
					allMovesValues.put(move, distanceValue);
				}
			}
		}
		// best move
		
			int bestDistance = Integer.MIN_VALUE;
			MOVE bestMove = null;
			for (MOVE move : possibilitiesMoves) {
				if(allMovesValues.get(move)!= null)
					if (allMovesValues.get(move)>bestDistance) {
						bestDistance=allMovesValues.get(move);
						bestMove=move;
					}
			}
			
			return bestMove;
		
		
	}
	private MOVE getPersureMove(Game game, GHOST ghost, MOVE[] possibilitiesMoves) {
		Map <MOVE, Integer> allMovesValues = new HashMap <MOVE,Integer>(possibilitiesMoves.length);
		int ghLocation = game.getGhostCurrentNodeIndex(ghost);
		int pcLocation = game.getPacmanCurrentNodeIndex();
		for (MOVE move : possibilitiesMoves) {
			int neighbour=game.getNeighbour(ghLocation,move);
			int distanceValue = game.getShortestPathDistance(pcLocation, neighbour);
			for (GHOST otherGhost: GHOST.values()) {	
				int oGhLocation = game.getGhostCurrentNodeIndex(otherGhost);
				int distancePcGh = game.getShortestPathDistance(pcLocation, oGhLocation);
				if (ghost != otherGhost && distancePcGh>0) {					
					int[] path= game.getShortestPath(pcLocation, oGhLocation); 
					path= game.getShortestPath(pcLocation, oGhLocation);
					for (int node : path) {
						if (node == neighbour && game.isGhostEdible(otherGhost)) {
							distanceValue -=50;
						}else if (node == neighbour){
							distanceValue -=90;
						}
					}
					allMovesValues.put(move, distanceValue);
				}
			}
			
		}
		// best move
		
			int bestDistance = Integer.MAX_VALUE;
			MOVE bestMove = null;
			
			for (MOVE move : possibilitiesMoves) {
				if(allMovesValues.get(move)!= null)
					if (allMovesValues.get(move)<bestDistance) {
						bestDistance=allMovesValues.get(move);
						bestMove=move;
					}
			} 
			return bestMove;
		

	}
	
}
