package com.br.avalicao.finch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.br.avalicao.finch.controller.PartidaFultebolController;
import com.br.avalicao.finch.model.PartidaFutebol;
import com.br.avalicao.finch.repository.PartidaFutebolRepository;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class AvaliacaoFinchApplication {

	Logger logger = LoggerFactory.getLogger(AvaliacaoFinchApplication.class);
	
	@Autowired
	private PartidaFutebolRepository partidaFutebolRepository;
	
	private SimpleDateFormat formatoCompleto = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private SimpleDateFormat formatoData = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat formatoDataResumido = new SimpleDateFormat("dd/MM/yyyy");	
	private SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
	
	public static void main(String[] args){
		SpringApplication.run(AvaliacaoFinchApplication.class, args);
	}
	
	@Bean 
	public CommandLineRunner commandLineRunner(PartidaFutebolRepository partidaFutebolRepository) {
		return args -> {
			System.out.println("Iniciando o programa...");
		};
	}
	
	//configurando as Threads para trabalhar de forma assincrona
    @Bean(name = "extractExecutor")
    public Executor asyncExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);//maximo de 3 threads
        executor.initialize();
        return executor;
    }
    
    @Scheduled(fixedDelay = 259200000)//executa a função a cada 3 dias    
    public void iniciaExtracao() {
    	extraiHoje();//extrai os jogos do dia de hoje
    	extraiSegundoDia();//extrai os jogos do dia seguinte
    	extraiTerceiroDia();//extrai os jogos do terceiro dia
    }
	
    @Async("extractExecutor")
    public void extraiHoje() {
    	salvarJogos(null);
    }
    @Async("extractExecutor")
    public void extraiSegundoDia() {
    	Date data = new Date();
    	data.setDate(data.getDate()+1);
    	salvarJogos(data);
    }
    @Async("extractExecutor")
    public void extraiTerceiroDia() {
    	Date data = new Date();
    	data.setDate(data.getDate()+2);
    	salvarJogos(data);
    }    
    
	public void salvarJogos(Date data) {
    	System.out.println(data);
		List<PartidaFutebol> listaextraida = extairJogos(data);
		if(listaextraida!=null && !listaextraida.isEmpty()) {
			partidaFutebolRepository.saveAll((Iterable<PartidaFutebol>) listaextraida);			
			for(PartidaFutebol partida : listaextraida) {									
				logger.info("-------------------- " + partida.getCampeonato() +" --------------------");					
				logger.info("Partida: "+partida.toString());
				logger.info("Data: "+formatoDataResumido.format(partida.getDataPartida()));
				logger.info("Hora da partida " + formatoHora.format(partida.getDataPartida()));
				logger.info("Mandante do jogo: "+partida.getTimeMandante());
				logger.info("Time visitante: "+partida.getTimeVisitante());
				logger.info("--------------------------------------------------------------");	
			}
		}
	}
	
	public List<PartidaFutebol> extairJogos(Date data) {
		List<PartidaFutebol> listaPartidas = null;
		
		try (final WebClient webClient = new WebClient()) {
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setCssEnabled(false);
						
			String url = "https://globoesporte.globo.com/placar-ge/hoje/jogos.ghtml";
			
			if(data!=null) {				
				url = "https://globoesporte.globo.com/placar-ge/"+formatoData.format(data)+"/jogos.ghtml";
			}			

			final HtmlPage page = webClient.getPage(url);
			
			logger.info(page.getTitleText());				
			final DomNodeList<DomNode> divs = page.querySelectorAll("article.card-jogo");
			logger.info("Listando jogos ("+divs.size()+")");
			
			if(!divs.isEmpty()) {
				listaPartidas = new ArrayList<PartidaFutebol>();
				for(DomNode domNode : divs) {
					String campeonato = domNode.querySelectorAll("h1 span").get(0).asText();
					String datahora = domNode.querySelector("h1").querySelector("time").getAttributes().getNamedItem("datetime").getNodeValue();
					String timeMandante = domNode.querySelectorAll("div .mandante").get(0).asText();
					String timeVisitante = domNode.querySelectorAll("div .visitante").get(0).asText();
					
					Date dataPartida = formatoCompleto.parse(datahora);
					
					PartidaFutebol pf = new PartidaFutebol();
					pf.setId(null);
					pf.setCampeonato(campeonato);
					pf.setTimeMandante(timeMandante);
					pf.setTimeVisitante(timeVisitante);
					pf.setDataPartida(dataPartida);
					listaPartidas.add(pf);									
				}
			}       
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}finally {
			return listaPartidas;
		}
	}
}
