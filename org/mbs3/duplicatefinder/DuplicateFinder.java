/*
 *   Copyright 2006 Martin B. Smith
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
