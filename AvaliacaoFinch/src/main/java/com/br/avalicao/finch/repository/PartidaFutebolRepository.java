package com.br.avalicao.finch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.br.avalicao.finch.model.PartidaFutebol;

public interface PartidaFutebolRepository extends PagingAndSortingRepository<PartidaFutebol, Long>{

}
