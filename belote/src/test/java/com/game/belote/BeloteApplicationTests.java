package com.game.belote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.belote.entity.Card;
import com.game.belote.entity.Game;
import com.game.belote.entity.Player;
import com.game.belote.entity.Suit;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BeloteApplicationTests {
	@PostConstruct
	void prerequisite() {
		RestAssured.baseURI = "http://localhost:8080/api/game";
	}

	@Test
	void setupGame() throws JsonProcessingException {

		LinkedList<String> playerNames = new LinkedList<>(List.of("Marko", "Nenad", "Igor", "Petar"));
		Collections.shuffle(playerNames);

		RequestSpecification request = RestAssured.given();
		Response response;
		JsonPath jsonPath;

		//creating game with first name from playerNames list
		response = request.post("/%s".formatted(playerNames.get(0)));
		//System.out.println(response.asString());
		assertTrue(response.getStatusCode() == 200);
		jsonPath = response.jsonPath();
		String gameId = jsonPath.getString("id");

		//joining rest of the players to game with game id
		for(int i = 1; i < playerNames.size(); i++) {
			response = request.post("/%s/%s".formatted(gameId, playerNames.get(i)));
			//System.out.println(response.asString());
			assertTrue(response.getStatusCode() == 200);
		}

		//starting the game
		response = request.post("/%s/start".formatted(gameId));
		jsonPath = response.jsonPath();
		//System.out.println(response.asString());
		assertTrue(response.getStatusCode() == 200);

		//randomly pick name and choose suit
		Game game = randomlyChooseSuit(gameId, playerNames, jsonPath);

		//start throwing cards
		game = throwCards(game);

		System.out.println("-".repeat(300));
		System.out.print(""" 
  	%s/%-10s%s/%-10s
  	%-10d%10d
			""".formatted(playerNames.get(0),
				playerNames.get(2),
				playerNames.get(1),
				playerNames.get(3),
				game.getTeamSums().get("team1"),
				game.getTeamSums().get("team2")));

		//game.getPlayers().forEach(p -> System.out.printf("%s's hand: %s%n", p.getName(), p.getHand()));
	}

	private Game randomlyChooseSuit(String gameId, LinkedList<String> playerNames, JsonPath jsonPath) throws JsonProcessingException {
		//randomly pick name and choose suit
		RequestSpecification request = RestAssured.given();
		Response response = null;
		String randomPlayerToChooseSuit = playerNames.get(new Random().nextInt(4));
		System.out.println("-".repeat(300));
		System.out.println("Randomly chosen player to pick the suit is %s".formatted(randomPlayerToChooseSuit));
		String playerNameOnTurn = jsonPath.getString("turn.name");
		System.out.println("First player on turn to pick the suit is %s%n".formatted(playerNameOnTurn));
		while(!playerNameOnTurn.equals(randomPlayerToChooseSuit)) {
			System.out.println("Game %s: Player %s skipped choosing the suit".formatted(gameId, playerNameOnTurn));
			response = request.post("/%s/%s/suit".formatted(gameId, playerNameOnTurn));
			jsonPath = response.jsonPath();
			assertTrue(response.getStatusCode() == 200);
			assertTrue(jsonPath.getString("suit") == null);
			playerNameOnTurn = jsonPath.getString("turn.name");
		}
		if(playerNameOnTurn.equals(randomPlayerToChooseSuit)) {
			String chosenSuit = Suit.randomSuit().name();
			response = request.post("/%s/%s/suit/%s".formatted(gameId, playerNameOnTurn, chosenSuit));
			jsonPath = response.jsonPath();
			assertTrue(jsonPath.getString("suit").equals(chosenSuit));
			System.out.println("Game %s: Player %s chose %s".formatted(gameId, playerNameOnTurn, chosenSuit));
		}

		ObjectMapper objectMapper = new ObjectMapper();
		Game game = objectMapper.readValue(response.getBody().asString(), Game.class);

		return game;
	}

	private Game throwCards(Game game) {
		boolean allCardsThrown = false;
		ObjectMapper objectMapper = new ObjectMapper();
		RequestSpecification request = RestAssured.given();

		while(!allCardsThrown) {
			String playerNameOnTurn = game.getTurn().getName();
			Card randomCardFromHand = game.getTurn().getHand().get(new Random().nextInt(game.getTurn().getHand().size()));
			JSONObject requestParams = new JSONObject();
			requestParams.put("suit", randomCardFromHand.getSuit());
			requestParams.put("face", randomCardFromHand.getFace());
			request.body(requestParams);
			Response response = request
					.contentType("application/json")
					.post("/%s/%s/throw".formatted(game.getId(), playerNameOnTurn));
			try {
				game = objectMapper.readValue(response.getBody().asString(), Game.class);
			}
			catch (Exception exception) {
				//ex.printStackTrace();
			}
			finally {
				Integer num = game.getRounds().stream()
						.map(Map::size)
						.reduce(0, Integer::sum);
				allCardsThrown = num.equals(32);
			}
		}

		return game;
	}

	@Test
	public void getBonusTest() throws JsonProcessingException {
		boolean flag = false;
		while(!flag) {
			System.out.println("-".repeat(300));
			System.out.println("-".repeat(300));
			System.out.println("NEW GAME...");
			System.out.println("-".repeat(300));
			LinkedList<String> playerNames = new LinkedList<>(List.of("Marko", "Nenad", "Igor", "Petar"));
			Collections.shuffle(playerNames);

			RequestSpecification request = RestAssured.given();
			Response response;
			JsonPath jsonPath;

			//creating game with first name from playerNames list
			response = request.post("/%s".formatted(playerNames.get(0)));
			//System.out.println(response.asString());
			assertTrue(response.getStatusCode() == 200);
			jsonPath = response.jsonPath();
			String gameId = jsonPath.getString("id");

			//joining rest of the players to game with game id
			for(int i = 1; i < playerNames.size(); i++) {
				response = request.post("/%s/%s".formatted(gameId, playerNames.get(i)));
				//System.out.println(response.asString());
				assertTrue(response.getStatusCode() == 200);
			}

			//starting the game
			response = request.post("/%s/start".formatted(gameId));
			jsonPath = response.jsonPath();
			//System.out.println(response.asString());
			assertTrue(response.getStatusCode() == 200);

			//randomly pick name and choose suit
			Game game = randomlyChooseSuit(gameId, playerNames, jsonPath);

			for(int i = 0; i < 4; i++) {
				Player player = game.getPlayers().get(i);
				Map<String, List<Card>> cards = game.calculateBonus(player.getName(), player.getHand());
				if(!cards.isEmpty()) {
					flag = true;
					break;
				}
			}
		}
	}
}
