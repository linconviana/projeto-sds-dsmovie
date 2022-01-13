package com.devsuperior.dsmovie.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.Movie;
import com.devsuperior.dsmovie.entities.Score;
import com.devsuperior.dsmovie.entities.User;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.repositories.UserRepository;

@Service
public class ScoreService {

	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ScoreRepository scoreRepository;
	
	@Transactional
	public MovieDTO saveScore (ScoreDTO dto) {
		
		/// :: Buscar usuario por email
		User user = userRepository.findByEmail(dto.getEmail());
		
		/// :: Verificar se existe usuario cadastrado
		if(user == null) {
			user = new User();
			user.setEmail(dto.getEmail());
			user = userRepository.saveAndFlush(user);
		}
		
		/// :: Buscar dados do filme
		Movie movie = movieRepository.findById(dto.getMovieId()).get();
		
		Score score = new Score();
		score.setMovie(movie);
		score.setMovie(user);
		score.setValue(dto.getScore());
		
		/// :: Salvar o score
		score = scoreRepository.saveAndFlush(score);
		
		/// :: Pegar a soma das notas dos filmes
		double sum = 0.0;
		for (Score s : movie.getScores()) {
			sum = sum + s.getValue();
		}
		
		/// :: Calcular nova media
		double avg = sum / movie.getScores().size();
		
		movie.setScore(avg);
		movie.setCount(movie.getScores().size());
		
		/// :: Salvar filme com informaçõea atualizadas
		movie  = movieRepository.save(movie);
		
		return new MovieDTO(movie);
	}
}
