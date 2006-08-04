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

import java.io.*;
import org.apache.commons.codec.digest.*;

/*
 * Created on Jul 15, 2006
 *
 * TODO Nothing yet.
 */

public class FileEntry implements Comparable
{

    private String crc;
    private File file;
    
    public FileEntry (File file)
    {
        super();
        this.file = file;
    }

    public FileEntry (String file)
    {
        this.file = new File(file);
    }
    
    public void computeCRC()
    {
        try {
            int size = (int)file.length();
        
        byte [] b = new byte[size];
        
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
        for(int i = 0; i < size; i++)
            b[i] = (byte)is.read();
        
        crc = DigestUtils.md5Hex(b);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    
    /**
     * @return Returns the value of file.
     */
    public File getFile ()
    {
        return this.file;
    }

    
    /**
     * @param Sets file to file.
     */
    public void setFile (File file)
    {
        this.file = file;
    }
    
    /**
     * @return Returns the value of crc.
     */
    public String getCrc ()
    {
        return this.crc;
    }
    
    public int compareTo(Object o)
    {
        FileEntry second = (FileEntry)o;
        if(second.getCrc().equals(this.getCrc()))
            return 0;
        return 1;
    }
}
