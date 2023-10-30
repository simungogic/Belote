package com.game.belote;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BeloteApplicationTests {
	@PostConstruct
	void prereq() {
		RestAssured.baseURI = "http://localhost:8080/api/game";
	}

	@Test
	void contextLoads() {
	}

	@Test
	void end2end() {
		LinkedList<String> playerNames = new LinkedList<>(List.of("Marko", "Nenad", "Igor", "Petar"));
		Collections.shuffle(playerNames);

		RequestSpecification request = RestAssured.given();
		Response response;
		JsonPath jsonPath;

		//creating game with first name from playerNames list
		response = request.post("/%s".formatted(playerNames.get(0)));
		System.out.println(response.asString());
		assertTrue(response.getStatusCode() == 200);
		jsonPath = response.jsonPath();
		String gameId = jsonPath.get("id");

		//joining rest of the players to game with game id
		for(int i = 1; i < playerNames.size(); i++) {
			response = request.post("/%s/%s".formatted(gameId, playerNames.get(i)));
			System.out.println(response.asString());
			assertTrue(response.getStatusCode() == 200);
		}

		//starting the game
		response = request.post("/%s/start".formatted(gameId));
		System.out.println(response.asString());
		assertTrue(response.getStatusCode() == 200);

		//get turn and second one pick the suit
		jsonPath = response.jsonPath();
		String playerNameOnTurn = jsonPath.get("turn.name");
		response = request.post("/%s/%s/suit".formatted(gameId, playerNameOnTurn));
		jsonPath = response.jsonPath();
		assertTrue(response.getStatusCode() == 200);
		assertTrue(jsonPath.get("suit") == null);
		response = request.post("/%s/%s/suit/crvena".formatted(gameId, playerNames.get(playerNames.indexOf(playerNameOnTurn) + 1)));
		jsonPath = response.jsonPath();
		assertTrue(jsonPath.getString("suit").equals("CRVENA"));
		System.out.println(response.asString());

		//throwing cards
		playerNameOnTurn = jsonPath.get("turn.name");
		Iterator<String> playerNameIterator = playerNames.listIterator(playerNames.indexOf(playerNameOnTurn));
		while(playerNameIterator.hasNext()) {
			String playerName = playerNameIterator.next();
			response = request.post("/%s/%s/suit/crvena".formatted(gameId, playerNames.get(playerNames.indexOf(playerNameOnTurn) + 1)));

			if(!playerNameIterator.hasNext()) {
				playerNameIterator = playerNames.listIterator();
			}
		}
	}
}
