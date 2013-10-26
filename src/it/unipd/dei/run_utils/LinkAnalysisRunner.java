package it.unipd.dei.run_utils;

import it.unimi.dsi.big.webgraph.EFGraph;
import it.unimi.dsi.big.webgraph.ImmutableGraph;
import it.unimi.dsi.big.webgraph.LazyLongIterator;
import it.unipd.dei.algorythms.MyGraphUtils;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Scanner;

public class LinkAnalysisRunner {
	private final static String PAGE_RANK="page rank";
	private final static String END="end";
	private static final String CHECK="check";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<1){
			System.err.println("usage:\njava -jar <jar_name.jar> <basename_bvGraph>");
			System.exit(1);
		}
		try {
			EFGraph graph=EFGraph.loadOffline(args[0]);
			Scanner in=new Scanner(System.in);
			String command=null;
			do{
				System.out.println(PAGE_RANK+" - executes page rank");
				System.out.println(CHECK+" - look the graph and print out poblems, checking the nodes and asserting whether they are >=0 and <=|V|");
				System.out.println(END+" - close the application");
				command=in.nextLine();
				switch (command) {
				case PAGE_RANK:
					System.out.println("write alpha:");
					double alpha=in.nextDouble();
					System.out.println("write epsilon");
					double epsilon=in.nextDouble();
					System.out.println("write max iteration");
					int maxIterations=in.nextInt();
					try {
						double[] page_rank_res=MyGraphUtils.GetPageRank(graph, alpha, epsilon, maxIterations);
						for(double d: page_rank_res){
							System.out.print(d+" - ");
						}
					} catch (InvalidAlgorithmParameterException e) {
						e.printStackTrace();
					}
					break;
				case CHECK:
				default:
					break;
				}
			} while (END.equals(command));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void check(ImmutableGraph graph){
		int n=graph.intNumNodes();
		for(int i=0; i<n; i++){
			LazyLongIterator successors=graph.successors(i);
			int outDegree=(int)graph.outdegree(i);
			long errors=0;
			for(int j=0; j<outDegree; j++){
				int succ=(int)successors.nextLong();
				if(succ>n || succ <0){
					errors++;
				}
			}
			System.out.println("node "+i+": errors//total="+errors+"//"+outDegree);
			errors=0;
			outDegree=0;
		}
	}

}
