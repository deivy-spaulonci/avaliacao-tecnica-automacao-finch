package com.br.avalicao.finch.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class PartidaFutebol  implements Serializable{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name="TIME_MANDANTE", nullable = false)
    private String TimeMandante;
    
    @Column(name="TIME_VISITANTE", nullable = false)
    private String TimeVisitante;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_PARTIDA", nullable = false)
    private Date dataPartida;
    
    @Column(name="CAMPEONATO", nullable = false)
    private String campeonato;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTimeMandante() {
		return TimeMandante;
	}

	public void setTimeMandante(String timeMandante) {
		TimeMandante = timeMandante;
	}

	public String getTimeVisitante() {
		return TimeVisitante;
	}

	public void setTimeVisitante(String timeVisitante) {
		TimeVisitante = timeVisitante;
	}

	public Date getDataPartida() {
		return dataPartida;
	}

	public void setDataPartida(Date dataPartida) {
		this.dataPartida = dataPartida;
	}

	public String getCampeonato() {
		return campeonato;
	}

	public void setCampeonato(String campeonato) {
		this.campeonato = campeonato;
	}

	@Override
	public String toString() {
		return TimeMandante + " X " + TimeVisitante;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartidaFutebol other = (PartidaFutebol) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
    

}
