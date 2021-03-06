package it.unipd.dei.run_utils;

import it.unimi.dsi.big.webgraph.BVGraph;
import it.unimi.dsi.big.webgraph.ImmutableGraph;
import it.unimi.dsi.big.webgraph.LazyLongIterator;
import it.unipd.dei.algorithms.MyGraphUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class LinkAnalysisRunner {
	private final static String PAGE_RANK="page rank";
	private final static String END="end";
	private static final String CHECK="check";
	private static String OUT_PATH=".pagerank";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<1){
			System.err.println("usage:\njava -jar <jar_name.jar> <basename_bvGraph>");
			System.exit(1);
		}
		try {
			BVGraph graph=BVGraph.loadOffline(args[0]);
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
						MyGraphUtils.setVerboseLevel(1);
						DateFormat df=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
						Date start=Calendar.getInstance().getTime();
						double[] page_rank_res=MyGraphUtils.GetPageRank(graph, alpha, epsilon, maxIterations);
						Date end=Calendar.getInstance().getTime();
						String startS=df.format(start);
						String endS=df.format(end);
						String diff=df.format(new Date(end.getTime()-start.getTime()));
						System.out.println("Start time: "+startS+"\nEnd time: "+endS+"\ndiff: "+diff);
						File outFile=new File(args[0]+OUT_PATH);
						outFile.createNewFile();
						FileWriter out=new FileWriter(outFile);
						for(int i=0; i<page_rank_res.length; i++){
							out.write(i+" "+page_rank_res[i]+"\n");
						}
						out.close();
					} catch (InvalidAlgorithmParameterException e) {
						e.printStackTrace();
					}
					break;
				case CHECK:
					System.out.println("verbose on? (y/n)");
					boolean verbose=!in.nextLine().equals("n");
					check(graph, verbose);
				default:
					break;
				}
			} while (END.equals(command));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void check(ImmutableGraph graph, boolean verbose){
		long totalError=0;
		long totalOutDegree=0;
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
			if(verbose){
				System.out.println("node "+i+": errors/total="+errors+"/"+outDegree);
			}
			totalError+=errors;
			totalOutDegree+=outDegree;
			if(totalError<0){
				System.err.println("OVERFLOW of errors... continuing anyway");
			}
			errors=0;
			outDegree=0;
		}
		System.out.println("TOTAL: errors/totOut = "+totalError+"/"+totalOutDegree);
	}
}
