package com.example.demo.service;

import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class MazeSolverService {

    private final DefaultApi api;

    private static class Cell {
        Position pos;
        Map<Direction, Boolean> walls = new EnumMap<>(Direction.class);
        Cell(Position p) { this.pos = p; }
    }

    @Autowired
    public MazeSolverService(ApiClient client) {
        this.api = new DefaultApi(client);
    }

    public void solve(String groupName) throws ApiException {
        // Spiel starten
        Game game = api.gamePost(new GameInput().groupName(groupName));
        BigDecimal gameId = game.getGameId();
        Position start = game.getPosition();
        assert start != null;
        System.out.printf("Spiel %bd gestartet bei (%bd,%bd)\n", gameId, start.getPositionX(), start.getPositionY());

        // Labyrinth-Datenstruktur
        Map<String, Cell> maze = new HashMap<>();
        Cell startCell = new Cell(start);
        maze.put(key(start), startCell);

        // DFS mit Backtracking
        dfsExplore(gameId, startCell, maze, new HashSet<>());
    }

    private boolean dfsExplore(BigDecimal gameId, Cell current, Map<String, Cell> maze, Set<String> visited) throws ApiException {
        String key = key(current.pos);
        visited.add(key);

        // Prüfen, ob Exit erreicht ist
        Game status = api.gameGameIdGet(gameId);
        if (status.getStatus() == GameStatus.SUCCESS) {
            System.out.println("Exit erreicht!");
            return true;
        }

        // Alle Richtungen erkunden
        for (Direction dir : Direction.values()) {
            if (current.walls.containsKey(dir) && !current.walls.get(dir)) {
                // bekannter freier Weg
                if (moveAndExplore(gameId, current, dir, maze, visited)) return true;
            } else if (!current.walls.containsKey(dir)) {
                // noch nicht erkundet
                if (moveAndExplore(gameId, current, dir, maze, visited)) return true;
            }
        }

        // Backtrack
        visited.remove(key);
        return false;
    }

    private boolean moveAndExplore(BigDecimal gameId, Cell from, Direction dir, Map<String, Cell> maze, Set<String> visited) throws ApiException {
        // Move versenden
        Move mv = api.gameGameIdMovePost(gameId, new MoveInput().direction(dir));
        boolean moved = mv.getMoveStatus() == MoveStatus.MOVED;

        // Wand oder freier Weg
        from.walls.put(dir, !moved);
        if (!moved) return false;

        // Neue Position
        Position p = mv.getPositionAfterMove();
        assert p != null;
        String keyNew = key(p);
        Cell next = maze.computeIfAbsent(keyNew, k -> new Cell(p));
        next.walls.put(opposite(dir), false);

        // Falls ungeprüft, rekursiv weiter
        if (!visited.contains(keyNew)) {
            if (dfsExplore(gameId, next, maze, visited)) return true;
        }

        // Zurückbewegen
        api.gameGameIdMovePost(gameId, new MoveInput().direction(opposite(dir)));
        return false;
    }

    private static String key(Position p) {
        return p.getPositionX() + ":" + p.getPositionY();
    }

    private static Direction opposite(Direction dir) {
        return switch (dir) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
            default -> throw new IllegalArgumentException("Unbekannte Richtung: " + dir);
        };
    }
}
