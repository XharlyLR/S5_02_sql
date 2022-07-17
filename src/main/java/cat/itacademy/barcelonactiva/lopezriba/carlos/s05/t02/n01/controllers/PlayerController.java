package cat.itacademy.barcelonactiva.lopezriba.carlos.s05.t02.n01.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import cat.itacademy.barcelonactiva.lopezriba.carlos.s05.t02.n01.model.domain.Daus;
import cat.itacademy.barcelonactiva.lopezriba.carlos.s05.t02.n01.model.domain.Player;
import cat.itacademy.barcelonactiva.lopezriba.carlos.s05.t02.n01.model.services.DausService;
import cat.itacademy.barcelonactiva.lopezriba.carlos.s05.t02.n01.model.services.PlayerService;

@Controller
@RequestMapping("/players")
public class PlayerController {

	@Autowired
	PlayerService playerService;
	
	@Autowired
	DausService dausService;
	
	@PostMapping("")
	public ResponseEntity<Player> addPlayer(@RequestBody Player player){
		try {
			Player _player = playerService.savePlayer(new Player(player.getNomPlayer()));
			return new ResponseEntity<>(_player, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Player> updatePlayer(@PathVariable("id") long id, @RequestBody Player player) {
		Optional<Player> playerData = playerService.findPlayerById(id);
		if (playerData.isPresent()) {
			Player _player = playerData.get();
			_player.setNomPlayer(player.getNomPlayer());
			return new ResponseEntity<>(playerService.savePlayer(_player), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	public ResponseEntity<Daus> addDaus(@RequestBody Player player){
		try {
			Daus daus = dausService.saveDaus(new Daus(player.getPlayerID()));
			
			if (daus.getExit())
				player.setPercExit(1);
			else
				player.setPercExit(0);
			
			playerService.savePlayer(player);
			return new ResponseEntity<>(daus, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/{id}/games/")
	public ResponseEntity<?> gamePlayer(@PathVariable("id") long id) {
		Optional<Player> playerData = playerService.findPlayerById(id);
		if (playerData.isPresent()) {
			Player _player = playerData.get();
			return new ResponseEntity<>(addDaus(_player), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/{id}/games")
	public ResponseEntity<HttpStatus> deleteDaus(@PathVariable("id") long id) {
		try {
			Optional<Player> playerData = playerService.findPlayerById(id);
			if (playerData.isPresent()) {
				Player player = playerData.get();
				player.setPercExit(0);
				playerService.savePlayer(player);
				dausService.deleteAllDausByPlayerID(id);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} 
			else
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/")
	public ResponseEntity<List<Player>> getAllPlayer() {
		try {
			List<Player> player = new ArrayList<Player>();
			playerService.findAllPlayer().forEach(player::add);
			
			if (player.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(player, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/{id}/games")
	public ResponseEntity<List<Daus>> getAllDaus(@PathVariable("id") long id) {
		try {
			List<Daus> daus = new ArrayList<Daus>();
			dausService.findAllDausByPlayerID(id).forEach(daus::add);
			
			if (daus.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(daus, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/ranking")
	public ResponseEntity<?> getRankingAll() {
		try {
			List<Player> players = new ArrayList<Player>();
			playerService.findAllPlayer().forEach(players::add);
			
			if (players.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
			int percAll = 0;
			
			for (Player player : players) {
				percAll += player.getPercExit();
			}
			percAll /= players.size();
			return new ResponseEntity<>("Exit mitjà de " + percAll + "%", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/ranking/loser")
	public ResponseEntity<Player> getRankingLoser() {
		try {
			List<Player> players = new ArrayList<Player>();
			playerService.findAllPlayer().forEach(players::add);
			
			if (players.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
			Player player = players.get(0);
			
			for (Player _player : players) {
				if (_player.getPercExit() < player.getPercExit())
					player = _player;
			}
			
			return new ResponseEntity<>(player, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/ranking/winner")
	public ResponseEntity<Player> getRankingWinner() {
		try {
			List<Player> players = new ArrayList<Player>();
			playerService.findAllPlayer().forEach(players::add);
			
			if (players.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
			Player player = players.get(0);
			
			for (Player _player : players) {
				if (_player.getPercExit() > player.getPercExit())
					player = _player;
			}
			
			return new ResponseEntity<>(player, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
