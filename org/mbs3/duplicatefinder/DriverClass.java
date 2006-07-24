package org.mbs3.duplicatefinder;

import java.util.*;
import org.apache.commons.cli.*;

/*
 * Created on Jul 15, 2006 TODO Nothing yet.
 */

/**
 * @author Martin Smith TODO None yet.
 */
public class DriverClass implements Runnable {

	public boolean recurse = false;

	public String path = new String();

	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		boolean recurse = false;
		String path = new String();

		// Option help = new Option( "help", "print this message" );
		// Option version = new Option( "version", "print the version
		// information and exit" );
		Option recurseOption = new Option("recurse",
				"recurse through all subdirectories of the given path");
		Option pathOption = OptionBuilder.withArgName("file").hasArg()
				.withDescription("search the given path").create("path");

		Options options = new Options();

		// options.addOption( help );
		// options.addOption( version );
		options.addOption(recurseOption);
		options.addOption(pathOption);
		// create the parser

		CommandLineParser parser = new GnuParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			// has the buildfile argument been passed?
			if (line.hasOption("path")) {
				// initialise the member variable
				path = line.getOptionValue("path");
			} else {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("DuplicateFinder", options);
				return;
			}

			if (line.hasOption("recurse")) {
				// initialise the member variable
				recurse = line.hasOption("recurse");
			}

		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}

		DriverClass dc = new DriverClass();
		dc.recurse = recurse;
		dc.path = path;
		Thread t = new Thread(null, dc, "DriverClass Thread");
		t.start();
	}

	public void run() {
		try {
			Vector stop = new Vector();
			DuplicateFinder df = new DuplicateFinder(path, recurse, stop);
			long wastedSpaceTotal = 0;

			System.out.println("Looking for duplicate files in " + path);
			Thread t = new Thread(null, df, "Search Thread");
			t.start();
			// while(df.getDuplicates().size() < 50)
			// {
			// Thread.yield();
			// System.err.println("loop");
			// }

			// System.out.println("Halting the search thread");
			// stop.add(new Object());

			t.join();
			// System.out.println("done searcing for files, checking for
			// dupes");
			@SuppressWarnings("unchecked")
			Hashtable<String,LinkedList<String>> dupes = (Hashtable<String,LinkedList<String>>)df.getAllFiles();

			Enumeration<String> e = dupes.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();

				Object temp = dupes.get(key);
				@SuppressWarnings("unchecked")
				LinkedList<String> dupeFiles = (LinkedList<String>) temp;

				int num = dupeFiles.size();
				if (num <= 1)
					continue;

				FileEntry first = new FileEntry(dupeFiles.getFirst());
				long singleFileSize = first.getFile().length();
				long wastedSpaceThisFile = (num-1) * first.getFile().length();
				wastedSpaceTotal += wastedSpaceThisFile;

				System.out.println(num + " files of size " + singleFileSize
						+ " bytes wasting " + wastedSpaceThisFile
						+ " bytes with identical checksums:");

				Iterator<String> i = dupeFiles.iterator();
				while (i.hasNext()) {
					FileEntry fe = new FileEntry(i.next());
					System.out.println("\t" + fe.getFile().getAbsolutePath());
					wastedSpaceThisFile += (fe.getFile().length() / 1024.0d);
				}
			}

			System.out
					.println("End of list - Total wasted space in duplicate files: "
							+ wastedSpaceTotal
							+ " bytes");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
