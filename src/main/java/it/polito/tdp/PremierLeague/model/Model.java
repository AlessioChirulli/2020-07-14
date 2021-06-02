package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	private PremierLeagueDAO dao;
	private Graph<Team,DefaultWeightedEdge> grafo;
	private Map<Integer,Team> idMap;
	private Simulator simulatore;
	
	public Model() {
		dao=new PremierLeagueDAO();
		
	}
	
	public void creaGrafo() {
		grafo=new SimpleDirectedWeightedGraph<Team,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		idMap=new HashMap<>();
		
		dao.listAllTeams(idMap);
		Graphs.addAllVertices(this.grafo, idMap.values());
		dao.creaClassifica(idMap);
		for(Team t1: idMap.values()) {
			for(Team t2:idMap.values()) {
				if(t1.getPuntiClassifica()>t2.getPuntiClassifica())
					Graphs.addEdge(grafo, t1, t2, t1.getPuntiClassifica()-t2.getPuntiClassifica());
				else if(t1.getPuntiClassifica()<t2.getPuntiClassifica())
					Graphs.addEdge(grafo, t2, t1, t2.getPuntiClassifica()-t1.getPuntiClassifica());
			}
		}
	}
	
	public String getSquadreMigliori(Team team){
		List<Team> result=new ArrayList<Team>();
		for(DefaultWeightedEdge e:grafo.incomingEdgesOf(team))
			result.add(grafo.getEdgeSource(e));
		
		Collections.sort(result);
		String s="Squadre migliori: \n";
		for(Team t: result) {
			s+=t.getName()+" "+grafo.getEdgeWeight(grafo.getEdge(t, team))+"\n";
		}
		return s;
	}
	
	public String getSquadrePeggiori(Team team){
		List<Team> result=new ArrayList<Team>();
		for(DefaultWeightedEdge e:grafo.outgoingEdgesOf(team))
			result.add(grafo.getEdgeTarget(e));
		
		Collections.sort(result,new ComparatoreSquadrePeggiori());
		String s="Squadre peggiori: \n";
		for(Team t: result) {
			s+=t.getName()+" "+grafo.getEdgeWeight(grafo.getEdge(team, t))+"\n";
		}
		return s;
	}
	
	
	public int getNVertex() {
		return grafo.vertexSet().size();
	}
	public int getNEdges() {
		return grafo.edgeSet().size();
	}
	public Set<Team> getVertex(){
		return grafo.vertexSet();
	}
	
	public class ComparatoreSquadrePeggiori implements Comparator<Team>{
	@Override
	public int compare(Team o1, Team o2) {
		
		return -(o1.getPuntiClassifica()-o2.getPuntiClassifica());
	}	
	}
	
	public String simula(int N,int soglia) {
		simulatore=new Simulator();
		simulatore.init(N,soglia, grafo, idMap);
		String s=("Partite sotto soglia: "+simulatore.getPartiteSottoSoglia()+"\nMedia report per partita: "+simulatore.getMedia());
		return s;
	}
}
