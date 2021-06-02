package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;
import it.polito.tdp.PremierLeague.model.Model.ComparatoreSquadrePeggiori;

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
						List<Team>squadreMigliori=this.getSquadreMigliori(p.getT1());
						int squadre=(int)(Math.random()*squadreMigliori.size());
						if(squadreMigliori.size()!=0) {
						Team t=squadreMigliori.get(squadre);
						p.getT1().menoReporter();
						t.plusReporter();
						}
						}
					}
					int prob1=(int)(Math.random()*100);
					if(prob1<=20) {
						int nRep=(int)(Math.random()*p.getT2().getReporter());
						List<Team>squadrePeggiori=this.getSquadrePeggiori(p.getT2());
						int squadre=(int)(Math.random()*squadrePeggiori.size());
						if(squadrePeggiori.size()!=0) {
						Team t=squadrePeggiori.get(squadre);
						p.getT2().setReporter(p.getT2().getReporter()-nRep);
						t.setReporter(t.getReporter()+nRep);
						}
					}
					break;
				case -1:
					int prob2=(int)(Math.random()*100);
					if(prob2<=50) {
						if(p.getT2().getReporter()>0) {
						List<Team>squadreMigliori=this.getSquadreMigliori(p.getT2());
						int squadre=(int)(Math.random()*squadreMigliori.size());
						if(squadreMigliori.size()!=0) {
						Team t=squadreMigliori.get(squadre);
								t.plusReporter();
								p.getT2().menoReporter();
						}
						}
					}
					int prob3=(int)(Math.random()*100);
					if(prob3<=20) {
						int nRep=(int)(Math.random()*p.getT1().getReporter());
						List<Team>squadrePeggiori=this.getSquadrePeggiori(p.getT2());
						int squadre=(int)(Math.random()*squadrePeggiori.size());
						if(squadrePeggiori.size()!=0) {
						Team t=squadrePeggiori.get(squadre);
						p.getT1().setReporter(p.getT1().getReporter()-nRep);
						t.setReporter(t.getReporter()+nRep);
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
		
		private List<Team> getSquadreMigliori(Team team){
			List<Team> result=new ArrayList<Team>();
			for(DefaultWeightedEdge e:grafo.incomingEdgesOf(team))
				result.add(grafo.getEdgeSource(e));
			
			Collections.sort(result);
			return result;
		}
		
		private List<Team> getSquadrePeggiori(Team team){
			List<Team> result=new ArrayList<Team>();
			for(DefaultWeightedEdge e:grafo.outgoingEdgesOf(team))
				result.add(grafo.getEdgeTarget(e));
			
			return result;
		}
}
