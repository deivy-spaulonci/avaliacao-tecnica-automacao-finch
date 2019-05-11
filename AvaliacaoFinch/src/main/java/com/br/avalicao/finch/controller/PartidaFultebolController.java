package com.br.avalicao.finch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.br.avalicao.finch.model.PartidaFutebol;
import com.br.avalicao.finch.repository.PartidaFutebolRepository;

@Controller
public class PartidaFultebolController {

	@Autowired
	private PartidaFutebolRepository partidaFutebolRepository;
	
	public void salvar(PartidaFutebol partidaFutebol) {
		partidaFutebolRepository.save(partidaFutebol);
	}
	
}
