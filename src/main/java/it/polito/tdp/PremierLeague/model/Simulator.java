package it.polito.tdp.PremierLeague.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Simulator {

		private List<Partita> partite;
		
		private int N;
		private Graph<Team,DefaultWeightedEdge> grafo;
		private Map<Integer,Team> idMap;
		private PremierLeagueDAO dao;
		
		private int nPartiteSottoSoglia;
		private int soglia;
		private int totReporter;
		
		public void init(int N,int soglia,Graph<Team,DefaultWeightedEdge> grafo,Map<Integer,Team> idMap) {
			this.N=N;
			this.soglia=soglia;
			this.idMap=idMap;
			this.grafo=grafo;
			this.dao=new PremierLeagueDAO();
			this.nPartiteSottoSoglia=0;
			this.totReporter=0;
			
			for(Team t:idMap.values()) {
				t.setReporter(N);
			}
			
			partite=new LinkedList<>();
			processEvent();
		}


		private void processEvent() {
			partite=dao.listAllPartite(idMap);
			
			for(Partita p: partite) {
				if(p.getT1().getReporter()+p.getT2().getReporter()<soglia)
					nPartiteSottoSoglia++;
				
				totReporter+=p.getT1().getReporter()+p.getT2().getReporter();
				
				switch (p.getRisultato()){
				case 1:
					int prob=(int)(Math.random()*100);
					if(prob<=50) {
						if(p.getT1().getReporter()>0) {
						p.getT1().menoReporter();
						for(Team t:idMap.values()) {
							if(t.getPuntiClassifica()>p.getT1().getPuntiClassifica()) {
								t.plusReporter();
								break;
							}
						}
						}
					}
					int prob1=(int)(Math.random()*100);
					if(prob1<=20) {
						int nRep=(int)(Math.random()*p.getT2().getReporter());
						p.getT2().setReporter(p.getT2().getReporter()-nRep);
						for(Team t:idMap.values()) {
							if(t.getPuntiClassifica()<p.getT2().getPuntiClassifica()) {
								t.setReporter(t.getReporter()+nRep);
								break;
							}
						}
					}
					break;
				case -1:
					int prob2=(int)(Math.random()*100);
					if(prob2<=50) {
						if(p.getT2().getReporter()>0) {
						p.getT2().menoReporter();
						for(Team t:idMap.values()) {
							if(t.getPuntiClassifica()>p.getT2().getPuntiClassifica()) {
								t.plusReporter();
								break;
							}
						}
						}
					}
					int prob3=(int)(Math.random()*100);
					if(prob3<=20) {
						int nRep=(int)(Math.random()*p.getT1().getReporter());
						p.getT1().setReporter(p.getT1().getReporter()-nRep);
						for(Team t:idMap.values()) {
							if(t.getPuntiClassifica()<p.getT1().getPuntiClassifica()) {
								t.setReporter(t.getReporter()+nRep);
								break;
							}
						}
					}
					break;
				}
			}
		}
	
		public double getMedia() {
			return (totReporter/partite.size());
		}
		public int getPartiteSottoSoglia() {
			return nPartiteSottoSoglia;
		}
}
