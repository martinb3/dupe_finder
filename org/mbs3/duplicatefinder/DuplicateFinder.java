package org.mbs3.duplicatefinder;

import java.util.*;
import java.io.*;

/*
 * Created on Jul 15, 2006
 *
 * TODO Nothing yet.
 */

/**
 * @author Martin Smith
 *
 * TODO None yet.
 */
public class DuplicateFinder implements Runnable
{

    private boolean recurse = false;
    private Vector stop;
    private String path = new String();
    
    // how about a Hashtable of linked lists, where each table entry
    // is a linked list of files with the same crc
    private Hashtable<String,LinkedList<String>> allFiles = new Hashtable<String,LinkedList<String>>();
    
    /**
     * 
     */
    public DuplicateFinder (String path, boolean recurse, Vector stop)
    {
        super();
        this.recurse = recurse;
        this.path = path;
        this.stop = stop;
    }

    public void run()
    {
        try {
            File root = new File(path);
            
            // traverse everything & checksum it
            traverse(root);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void traverse(File root)
    {
        File [] children = root.listFiles();
        for(int i = 0; i < children.length; i++)
        {
            if(this.stop.size() > 0)
                return;

            File current = children[i];
            if(current.canRead() && current.isFile())
            {
                FileEntry fe = new FileEntry(current);
                fe.computeCRC(); 
                
                Object currentCrc = allFiles.remove(fe.getCrc());
                if(currentCrc == null)
                {
                    LinkedList<String> ll = new LinkedList<String>();
                    ll.add(fe.getFile().getAbsolutePath());
                	allFiles.put(fe.getCrc(), ll);                    
                }
                else
                {
                	@SuppressWarnings("unchecked")
                	LinkedList<String> ll = (LinkedList<String>)(currentCrc);
                	ll.add(fe.getFile().getAbsolutePath());
                    allFiles.put(fe.getCrc(), ll);
                }
                fe = null;
                //System.out.println(current.getAbsolutePath() + ": " + fe.getCrc());
            } else if (current.canRead() && current.isDirectory() && recurse) 
            {
                Thread.yield();
                traverse(current);
            }
            Runtime.getRuntime().gc();
        }
    }

	public Hashtable getAllFiles() {
		return allFiles;
	}
	
}
